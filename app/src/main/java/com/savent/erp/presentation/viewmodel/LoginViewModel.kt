package com.savent.erp.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.savent.erp.ConnectivityObserver
import com.savent.erp.MyApplication
import com.savent.erp.R
import com.savent.erp.data.local.datasource.AppPreferencesLocalDatasource
import com.savent.erp.data.local.model.AppPreferences
import com.savent.erp.data.local.model.CompanyEntity
import com.savent.erp.data.local.model.StoreEntity
import com.savent.erp.utils.Resource
import com.savent.erp.domain.usecase.LoginUseCase
import com.savent.erp.presentation.ui.model.LoginError
import com.savent.erp.data.remote.model.LoginCredentials
import com.savent.erp.domain.repository.CompaniesRepository
import com.savent.erp.domain.repository.StoresRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val myApplication: Application,
    private val appPreferencesLocalDatasource: AppPreferencesLocalDatasource,
    private val companiesRepository: CompaniesRepository,
    private val storesRepository: StoresRepository,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _loginError = MutableLiveData(LoginError())
    val loginError: LiveData<LoginError> = _loginError

    private val _loggedIn = MutableLiveData(false)
    val loggedIn: LiveData<Boolean> = _loggedIn

    private var _networkStatus = ConnectivityObserver.Status.Available

    private val _selectedCompany = MutableLiveData<CompanyEntity>()
    val selectedCompany: LiveData<CompanyEntity> = _selectedCompany

    private val _selectedStore = MutableLiveData<StoreEntity>()
    val selectedStore: LiveData<StoreEntity> = _selectedStore

    private val _companies = MutableLiveData<List<CompanyEntity>>()
    val companies: LiveData<List<CompanyEntity>> = _companies

    private val _stores = MutableLiveData<List<StoreEntity>>()
    val stores: LiveData<List<StoreEntity>> = _stores

    private val clientsOptions = myApplication.resources.getStringArray(R.array.clients_filter)
    private val productsOptions = myApplication.resources.getStringArray(R.array.products_filter)

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class UiEvent {
        data class ShowMessage(val resId: Int? = null, val message: String? = null) : UiEvent()
    }

    private var networkObserverJob: Job? = null
    private var loginJob: Job? = null
    private var loadCompaniesJob: Job? = null
    private var reloadCompaniesJob: Job? = null
    private var loadStoresJob: Job? = null
    private var reloadStoresJob: Job? = null

    init {
        observeNetworkChange()
        loadCompanies()
        reloadCompanies()
        loadStores()
    }

    private fun observeNetworkChange() {
        networkObserverJob?.cancel()
        networkObserverJob = viewModelScope.launch(Dispatchers.Main) {
            (myApplication as MyApplication).networkStatus.collectLatest {
                if (_networkStatus != it) {
                    _networkStatus = it

                    if (it != ConnectivityObserver.Status.Losing
                        && it != ConnectivityObserver.Status.Unavailable
                    ) {
                        val resId = when (it) {
                            ConnectivityObserver.Status.Available -> R.string.online
                            else -> {
                                R.string.offline
                            }
                        }
                        _uiEvent.emit(UiEvent.ShowMessage(resId))
                    }
                }
            }
        }

    }

    fun login(loginCredentials: LoginCredentials) {
        loginJob?.cancel()
        loginJob = viewModelScope.launch(Dispatchers.IO) {
            if (!isInternetAvailable()) return@launch
            if (!isCompanySelected()) {
                _uiEvent.emit(UiEvent.ShowMessage(resId = R.string.select_company))
                return@launch
            }
            if (!isStoreSelected()) {
                _uiEvent.emit(UiEvent.ShowMessage(resId = R.string.select_store))
                return@launch
            }
            loginUseCase(
                loginCredentials,
                _selectedStore.value?.remoteId ?: 1,
                _selectedCompany.value?.remoteId ?: 1
            ).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _loading.postValue(true)
                    }
                    is Resource.Success -> {
                        loadDefaultPreferences()
                        _loading.postValue(false)
                        _loginError.postValue(LoginError())
                        _loggedIn.postValue(true)

                    }
                    is Resource.Error -> {
                        _loading.postValue(false)
                        /*_loginError.postValue(
                            Gson()
                                .fromJson(result.message, object : TypeToken<LoginError>() {}.type)
                        )*/
                        _uiEvent.emit(
                            UiEvent.ShowMessage(
                                result.resId ?: R.string.unknown_error
                            )
                        )
                    }
                }
            }
        }

    }

    suspend fun loadDefaultPreferences() {
        appPreferencesLocalDatasource.insertOrUpdateAppPreferences(
            AppPreferences(
                clientsOptions[clientsOptions.size - 1],
                productsOptions[productsOptions.size - 1],
                false
            )
        )
    }

    fun setCompany(company: CompanyEntity) {
        _selectedCompany.value = company
    }

    fun isCompanySelected(): Boolean = _selectedCompany.value?.remoteId != null

    fun loadCompanies(query: String = "") {
        loadCompaniesJob?.cancel()
        loadCompaniesJob = viewModelScope.launch(Dispatchers.IO) {
            companiesRepository.getCompanies(query).onEach {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {it1-> _companies.postValue(it1) }
                    }
                    else -> {
                        _uiEvent.emit(UiEvent.ShowMessage(it.resId, it.message))
                    }
                }

            }.collect()
        }

    }

    fun reloadCompanies() {
        reloadCompaniesJob?.cancel()
        reloadCompaniesJob = viewModelScope.launch(Dispatchers.IO) {
            if (!isInternetAvailable()) return@launch
            when (val result = companiesRepository.fetchCompanies()) {
                is Resource.Error -> {
                    _uiEvent.emit(UiEvent.ShowMessage(result.resId, result.message))
                }
                else -> {}
            }

        }
    }

    fun setStore(store: StoreEntity) {
        _selectedStore.value = store
    }

    fun isStoreSelected(): Boolean = _selectedStore.value?.remoteId != null

    fun loadStores(query: String = "") {
        loadStoresJob?.cancel()
        loadStoresJob = viewModelScope.launch(Dispatchers.IO) {
            if (!isCompanySelected()) {
                _uiEvent.emit(UiEvent.ShowMessage(resId = R.string.select_company))
                return@launch
            }
            storesRepository.getStores(query, _selectedCompany.value?.remoteId ?: 1).onEach {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {it1-> _stores.postValue(it1) }
                    }
                    else -> {
                        _uiEvent.emit(UiEvent.ShowMessage(it.resId, it.message))
                    }
                }

            }.collect()
        }

    }

    fun reloadStores() {
        reloadStoresJob?.cancel()
        reloadStoresJob = viewModelScope.launch(Dispatchers.IO) {
            if (!isInternetAvailable()) return@launch
            if (!isCompanySelected()) {
                _uiEvent.emit(UiEvent.ShowMessage(resId = R.string.select_company))
                return@launch
            }
            when (val result =
                storesRepository.fetchStores(_selectedCompany.value?.remoteId ?: 1)) {
                is Resource.Error -> {
                    _uiEvent.emit(UiEvent.ShowMessage(result.resId, result.message))
                }
                else -> {}
            }

        }
    }

    private suspend fun isInternetAvailable(): Boolean {
        if (_networkStatus != ConnectivityObserver.Status.Available) {
            _loading.postValue(false)
            _uiEvent.emit(UiEvent.ShowMessage(resId = R.string.internet_error))
            return false
        }
        return true
    }


}