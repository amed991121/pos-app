package com.savent.erp.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.savent.erp.ConnectivityObserver
import com.savent.erp.MyApplication
import com.savent.erp.R
import com.savent.erp.data.local.datasource.AppPreferencesLocalDatasource
import com.savent.erp.data.local.datasource.ClientsLocalDatasource
import com.savent.erp.data.local.model.AppPreferences
import com.savent.erp.data.local.model.BusinessBasicsLocal
import com.savent.erp.data.remote.datasource.ClientsRemoteDatasource
import com.savent.erp.data.remote.model.Client
import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.domain.usecase.*
import com.savent.erp.presentation.ui.model.ClientError
import com.savent.erp.presentation.ui.model.ClientItem
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ClientsViewModel(
    private val myApplication: Application,
    private val appPreferencesLocalDatasource: AppPreferencesLocalDatasource,
    private val businessBasicsRepository: BusinessBasicsRepository,
    private val createPendingSaleUseCase: CreatePendingSaleUseCase,
    private val getClientListUseCase: GetClientListUseCase,
    private val reloadClientsUseCase: ReloadClientsUseCase,
    private val removeAllClientsUseCase: RemoveAllClientsUseCase,
    private val saveNewClientUseCase: SaveNewClientUseCase,
    private val addClientToSaleUseCase: AddClientToSaleUseCase,
    private val validateClientUseCase: ValidateClientUseCase,
    private val clientsLocalDatasource: ClientsLocalDatasource,
    private val clientsRemoteDatasource: ClientsRemoteDatasource
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _networkStatus = MutableLiveData(ConnectivityObserver.Status.Available)
    val networkStatus: LiveData<ConnectivityObserver.Status> = _networkStatus

    private val _appPreferences = MutableLiveData<AppPreferences>()
    val appPreferences: LiveData<AppPreferences> = _appPreferences

    private var _businessBasics: BusinessBasicsLocal? = null

    private val _clients = MutableLiveData<List<ClientItem>>()
    val clients: LiveData<List<ClientItem>> = _clients

    private val _clientError = MutableLiveData<ClientError>()
    val clientError: LiveData<ClientError> = _clientError

    private var isClientSuccessfullyAdded = false

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class UiEvent {
        data class ShowMessage(val resId: Int? = null, val message: String? = null) : UiEvent()
        data class SaveClient(val success: Boolean = false) : UiEvent()
        data class Continue(val available: Boolean = false) : UiEvent()
    }

    private var loadDataJob: Job? = null
    private var loadClientsJob: Job? = null
    private var syncIODataJob: Job? = null
    private var reloadClientsJob: Job? = null
    private var createPendingSaleJob: Job? = null
    private var addClientJob: Job? = null
    private var updateClientJob: Job? = null
    private var networkObserverJob: Job? = null
    private var changeClientsFilterJob: Job? = null
    private var defaultJob: Job? = null

    init {
        loadData()
        observeNetworkChange()
        createPendingSale()

    }

    private fun observeNetworkChange() {
        networkObserverJob?.cancel()
        networkObserverJob = viewModelScope.launch(Dispatchers.IO) {
            _networkStatus.postValue((myApplication as MyApplication).currentNetworkStatus)
            myApplication.networkStatus.collectLatest {
                _networkStatus.postValue(it)
                if (it == ConnectivityObserver.Status.Available)
                    syncClientsIOData()
            }
        }

    }

    private fun loadData() {
        loadDataJob?.cancel()
        loadDataJob = viewModelScope.launch(Dispatchers.IO) {
            loadClients()
            async { loadAppPreferences() }
            loadBusinessBasics()
            delay(1000)
            if (isInternetAvailable())
                syncClientsIOData()
        }
    }

    private suspend fun loadAppPreferences() {
        appPreferencesLocalDatasource.getAppPreferences().onEach {
            if (it is Resource.Success && it.data != null) _appPreferences.postValue(it.data)
        }.collect()
    }

    private suspend fun loadBusinessBasics() {
        val businessBasics = businessBasicsRepository.getBusinessBasics().first()
        if (businessBasics is Resource.Success && businessBasics.data != null)
            _businessBasics = businessBasics.data
    }

    fun syncClientsIOData() {
        _loading.postValue(true)
        syncIODataJob?.cancel()
        syncIODataJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = RemoteClientSyncFromLocalUseCase.execute(
                _businessBasics?.sellerId ?: 0,
                _businessBasics?.storeId ?: 0,
                _businessBasics?.companyId ?: 1
            )) {
                is Resource.Error -> {
                    _loading.postValue(false)
                    _uiEvent.emit(
                        UiEvent.ShowMessage(result.resId, result.message)
                    )

                }
                is Resource.Success -> {
                    fetchClientsFromNetwork(_appPreferences.value?.clientsFilter ?: "")
                }
                else -> {
                    _loading.postValue(false)
                }
            }
        }
    }

    suspend fun isCreateClientAvailable(): Boolean {
        return businessBasicsRepository.getBusinessBasics().first().data?.sellerLevel!! > 2
    }

    fun changeClientsFilter(clientsFilter: String) {
        changeClientsFilterJob?.cancel()
        changeClientsFilterJob = viewModelScope.launch(Dispatchers.IO) {

            if (appPreferencesLocalDatasource.insertOrUpdateAppPreferences(
                    _appPreferences.value!!.copy(
                        clientsFilter = clientsFilter
                    )
                ) is Resource.Success
            )
                _appPreferences.postValue(_appPreferences.value!!.copy(clientsFilter = clientsFilter))

            if (isInternetAvailable()) {
                _loading.postValue(true)
                removeAllClientsUseCase()
                fetchClientsFromNetwork(clientsFilter)
            }

        }

    }

    private fun fetchClientsFromNetwork(clientsFilter: String) {
        reloadClientsJob?.cancel()
        reloadClientsJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = reloadClientsUseCase(
                _businessBasics!!.sellerId,
                _businessBasics!!.storeId,
                _businessBasics!!.companyId,
                clientsFilter
            )) {
                is Resource.Error -> {
                    _loading.postValue(false)
                    _uiEvent.emit(
                        UiEvent.ShowMessage(result.resId, result.message)
                    )

                }
                else -> {
                    _loading.postValue(false)
                }
            }
        }
    }

    fun loadClients(query: String = "") {
        loadClientsJob?.cancel()
        loadClientsJob = viewModelScope.launch(Dispatchers.IO) {
            getClientListUseCase(query).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        result.data?.let { _clients.postValue(it) }
                        if (result.data?.isEmpty() == true)
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    R.string.get_clients_empty
                                )
                            )
                    }
                    is Resource.Error -> {
                        _uiEvent.emit(
                            UiEvent.ShowMessage(
                                result.resId ?: R.string.unknown_error
                            )
                        )

                    }
                }

            }.collect()
        }
    }

    private fun createPendingSale() {
        createPendingSaleJob?.cancel()
        createPendingSaleJob = viewModelScope.launch(Dispatchers.IO) {
            createPendingSaleUseCase()
        }
    }

    fun saveNewClient(client: Client) {
        _loading.postValue(true)
        addClientJob?.cancel()
        addClientJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = validateClientUseCase(client)) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    _clientError.postValue(ClientError())
                }
                is Resource.Error -> {
                    _clientError.postValue(
                        Gson()
                            .fromJson(result.message, object : TypeToken<ClientError>() {}.type)
                    )
                    _uiEvent.emit(
                        UiEvent.ShowMessage(
                            result.resId ?: R.string.unknown_error
                        )
                    )
                    _loading.postValue(false)
                    return@launch
                }

            }
            if (!isInternetAvailable()) {
                _uiEvent.emit(UiEvent.ShowMessage(R.string.internet_error))
                return@launch
            }
            when (val result = clientsRemoteDatasource.insertClient(
                _businessBasics?.sellerId ?: 0,
                _businessBasics?.storeId ?: 0,
                client,
                _businessBasics?.companyId ?: 0
            )) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    when (val result1 = saveNewClientUseCase(client.copy(id = result.data ?: 0))) {
                        is Resource.Loading -> {
                        }
                        is Resource.Success -> {
                            coroutineScope {
                                _loading.postValue(false)
                                _uiEvent.emit(UiEvent.SaveClient(true))
                            }

                        }
                        is Resource.Error -> {
                            _loading.postValue(false)
                            _uiEvent.emit(UiEvent.SaveClient(false))
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    result1.resId, result1.message
                                )
                            )
                        }

                    }
                }
                is Resource.Error -> {
                    _loading.postValue(false)
                    _uiEvent.emit(UiEvent.SaveClient(false))
                    _uiEvent.emit(
                        UiEvent.ShowMessage(
                            result.resId, result.message
                        )
                    )
                }

            }
        }
    }

    fun addClientToSale(clientId: Int) {
        addClientJob?.cancel()
        addClientJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = addClientToSaleUseCase(clientId)) {
                is Resource.Success -> {
                    isClientSuccessfullyAdded = true
                }
                is Resource.Error -> {
                    _uiEvent.emit(
                        UiEvent.ShowMessage(
                            result.resId ?: R.string.unknown_error
                        )
                    )
                }

                else -> {}
            }
        }
    }

    fun await() {
        defaultJob?.cancel()
        defaultJob = viewModelScope.launch(Dispatchers.Main) {
            addClientJob?.join()
            if (!isClientSuccessfullyAdded) {
                _uiEvent.emit(UiEvent.ShowMessage(R.string.client_not_added))
                //defaultJob?.cancel()
                return@launch
            }
            _uiEvent.emit(UiEvent.Continue(true))
        }
    }

    fun updateClientLocation(latLng: LatLng, id: Int) {
        _loading.postValue(true)
        updateClientJob?.cancel()
        updateClientJob = viewModelScope.launch(Dispatchers.IO) {
            clientsLocalDatasource.getClient(id).let { clientEntity ->
                if (clientEntity is Resource.Success) {
                    val client = clientEntity.data
                    client?.copy(
                        location = latLng,
                        dateTimestamp = System.currentTimeMillis(),
                        pendingRemoteAction = PendingRemoteAction.UPDATE
                    )?.let {
                        when (val result = clientsLocalDatasource.updateClient(it)) {
                            is Resource.Loading -> {
                            }
                            is Resource.Success -> {
                                _loading.postValue(false)
                            }
                            is Resource.Error -> {
                                _loading.postValue(false)
                                _uiEvent.emit(
                                    UiEvent.ShowMessage(
                                        result.resId ?: R.string.unknown_error
                                    )
                                )
                            }

                        }
                    }
                } else {
                    _loading.postValue(false)
                    _uiEvent.emit(
                        UiEvent.ShowMessage(
                            R.string.get_clients_error
                        )
                    )
                }
            }

        }
    }

    suspend fun isInternetAvailable(): Boolean {
        if (_networkStatus.value != ConnectivityObserver.Status.Available) {
            _loading.postValue(false)
            _uiEvent.emit(UiEvent.ShowMessage(resId = R.string.internet_error))
            return false
        }
        return true
    }

}