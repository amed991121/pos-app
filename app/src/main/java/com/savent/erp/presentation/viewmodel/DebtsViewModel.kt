package com.savent.erp.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.savent.erp.ConnectivityObserver
import com.savent.erp.MyApplication
import com.savent.erp.R
import com.savent.erp.data.local.datasource.AppPreferencesLocalDatasource
import com.savent.erp.data.local.model.AppPreferences
import com.savent.erp.data.local.model.BusinessBasicsLocal
import com.savent.erp.data.remote.model.DebtPayment
import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.domain.repository.DebtPaymentRepository
import com.savent.erp.domain.usecase.*
import com.savent.erp.presentation.ui.model.*
import com.savent.erp.utils.DecimalFormat
import com.savent.erp.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class DebtsViewModel(
    private val myApplication: Application,
    private val appPreferencesLocalDatasource: AppPreferencesLocalDatasource,
    private val businessBasicsRepository: BusinessBasicsRepository,
    private val getClientListWithDebts: GetClientListWithDebts,
    private val getDebtPaymentsOfDayUseCase: GetDebtPaymentsOfDayUseCase,
    private val getDebtPaymentsBalanceUseCase: GetDebtPaymentsBalanceUseCase,
    private val getIncompletePaymentsUseCase: GetIncompletePaymentsUseCase,
    private val reloadIncompletePaymentsUseCase: ReloadIncompletePaymentsUseCase,
    private val reloadClientsUseCase: ReloadClientsUseCase,
    private val debtPaymentRepository: DebtPaymentRepository,
    private val payDebtUseCase: PayDebtUseCase,
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _appPreferences = MutableLiveData<AppPreferences>()
    val appPreferences: LiveData<AppPreferences> = _appPreferences

    private val _networkStatus = MutableLiveData(ConnectivityObserver.Status.Available)
    val networkStatus: LiveData<ConnectivityObserver.Status> = _networkStatus

    private val _clients = MutableLiveData<List<ClientWithDebtItem>>()
    val clients: LiveData<List<ClientWithDebtItem>> = _clients

    private val _debtPayments = MutableLiveData<List<DebtPaymentItem>>()
    val debtPayments: LiveData<List<DebtPaymentItem>> = _debtPayments

    private val _debtPaymentsBalance = MutableLiveData<DebtPaymentsBalance>()
    val debtPaymentsBalance: LiveData<DebtPaymentsBalance> = _debtPaymentsBalance

    private var _businessBasics: BusinessBasicsLocal? = null

    private val _incompletePayments = MutableLiveData<List<IncompletePaymentItem>>()
    val incompletePayments: LiveData<List<IncompletePaymentItem>> = _incompletePayments

    private val _clientDebt = MutableLiveData<ClientDebt>()
    val clientDebt: LiveData<ClientDebt> = _clientDebt

    private val _clientId = MutableLiveData<Int>()
    val clientId: LiveData<Int> = _clientId

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class UiEvent {
        data class ShowMessage(val resId: Int? = null, val message: String? = null) : UiEvent()
        object Back : UiEvent()
    }

    private var loadDataJob: Job? = null
    private var loadClientsJob: Job? = null
    private var loadDebtPaymentsJob: Job? = null
    private var reloadClientsJob: Job? = null
    private var loadIncompletePaymentsJob: Job? = null
    private var syncIODataJob: Job? = null
    private var payDebtJob: Job? = null
    private var networkObserverJob: Job? = null

    init {
        observeNetworkChange()
        loadBasicData()
    }

    private fun observeNetworkChange() {
        networkObserverJob?.cancel()
        networkObserverJob = viewModelScope.launch(Dispatchers.IO) {
            _networkStatus.postValue((myApplication as MyApplication).currentNetworkStatus)
            myApplication.networkStatus.collectLatest {
                _networkStatus.postValue(it)
                if (it == ConnectivityObserver.Status.Available) {
                    fetchClientsFromNetwork(_appPreferences.value?.clientsFilter ?: "")
                    syncDebtIOData()
                }
            }
        }

    }

    private fun loadBasicData() {
        loadDataJob?.cancel()
        loadDataJob = viewModelScope.launch(Dispatchers.IO) {
            loadClients()
            async { computeBalance() }
            async { loadAppPreferences() }
            loadBusinessBasics()
            delay(1000)
            if (isInternetAvailable()) {
                fetchClientsFromNetwork(_appPreferences.value?.clientsFilter ?: "")
                syncDebtIOData()
            }
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

    fun syncDebtIOData() {
        syncIODataJob?.cancel()
        syncIODataJob = viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            when (val result = RemoteDebtPaymentSyncFromLocalUseCase.execute(
                _businessBasics!!.id,
                _businessBasics!!.sellerId,
                _businessBasics!!.storeId,
                _businessBasics!!.companyId
            )) {
                is Resource.Error -> {
                    _loading.postValue(false)
                    _uiEvent.emit(
                        UiEvent.ShowMessage(result.resId, result.message)
                    )

                }
                is Resource.Success -> {
                    when (val result1 = reloadIncompletePaymentsUseCase(
                        _businessBasics!!.id,
                        _businessBasics!!.storeId,
                        _businessBasics!!.companyId
                    )) {
                        is Resource.Error -> {
                            _loading.postValue(false)
                            _uiEvent.emit(
                                UiEvent.ShowMessage(result1.resId, result1.message)
                            )

                        }
                        else -> {
                            _loading.postValue(false)
                        }
                    }
                    val response = debtPaymentRepository.fetchDebtPayments(
                        _businessBasics!!.id,
                        _businessBasics!!.storeId,
                    )
                    if (response is Resource.Error)
                        _uiEvent.emit(UiEvent.ShowMessage(response.resId, response.message))
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
            getClientListWithDebts(query).onEach { result ->
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

    fun loadDebtPayments(query: String = "") {
        loadDebtPaymentsJob?.cancel()
        loadDebtPaymentsJob = viewModelScope.launch(Dispatchers.IO) {
            getDebtPaymentsOfDayUseCase(query).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        result.data?.let { _debtPayments.postValue(it) }
                        if (result.data?.isEmpty() == true)
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    R.string.get_debt_payments_empty
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

    fun loadIncompletePayments(clientId: Int, clientDebt: ClientDebt) {
        _clientId.postValue(clientId)
        _clientDebt.postValue(clientDebt)
        loadIncompletePaymentsJob?.cancel()
        loadIncompletePaymentsJob = viewModelScope.launch(Dispatchers.IO) {
            getIncompletePaymentsUseCase(clientId).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        result.data?.let { _incompletePayments.postValue(it) }

                        if (result.data?.isEmpty() == true)
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    R.string.get_clients_empty
                                )
                            )

                        var actualTotalDebt = 0F
                        result.data?.forEach {
                            actualTotalDebt += it.debts
                        }
                        _clientDebt.postValue(
                            clientDebt.copy(
                                totalDebt = "$${
                                    DecimalFormat.format(
                                        actualTotalDebt
                                    )
                                }"
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

    private fun fetchClientsFromNetwork(clientsFilter: String) {
        reloadClientsJob?.cancel()
        reloadClientsJob = viewModelScope.launch(Dispatchers.IO) {
            reloadClientsUseCase(
                _businessBasics!!.sellerId,
                _businessBasics!!.storeId,
                _businessBasics!!.companyId,
                clientsFilter
            )
        }
    }

    private suspend fun computeBalance() {
        getDebtPaymentsBalanceUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { _debtPaymentsBalance.postValue(it) }
                }
                else -> {
                    _uiEvent.emit(UiEvent.ShowMessage(result.resId))
                }
            }

        }.collect()
    }

    fun payDebt(debtPayment: DebtPayment, saleId: Int) {
        payDebtJob?.cancel()
        payDebtJob = viewModelScope.launch(Dispatchers.IO) {
            async {
                payDebtUseCase(debtPayment, saleId)
            }.await()
            if (isInternetAvailable()) {
                RemoteDebtPaymentSyncFromLocalUseCase.execute(
                    _businessBasics!!.id,
                    _businessBasics!!.sellerId,
                    _businessBasics!!.storeId,
                    _businessBasics!!.companyId
                )
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

    fun onBackPressed() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.Back)
        }
    }


}