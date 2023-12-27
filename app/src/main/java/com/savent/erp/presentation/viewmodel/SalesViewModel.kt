package com.savent.erp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mazenrashed.printooth.data.printable.Printable
import com.savent.erp.ConnectivityObserver
import com.savent.erp.MyApplication
import com.savent.erp.R
import com.savent.erp.data.local.model.BusinessBasicsLocal
import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.domain.usecase.*
import com.savent.erp.presentation.ui.model.CashBalance
import com.savent.erp.presentation.ui.model.SaleItem
import com.savent.erp.presentation.ui.model.SalesBalance
import com.savent.erp.presentation.ui.model.SharedReceipt
import com.savent.erp.utils.DateFormat
import com.savent.erp.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

class SalesViewModel(
    private val myApplication: Application,
    private val businessBasicsRepository: BusinessBasicsRepository,
    private val getSalesOfDayUseCase: GetSalesOfDayUseCase,
    private val GetCashBalanceUseCase: GetCashBalanceUseCase,
    private val getSalesBalanceUseCase: GetSalesBalanceUseCase,
    private val reloadSalesUseCase: ReloadSalesUseCase,
    private val getReceiptToSend: GetReceiptToSend,
    private val getReceiptToPrint: GetReceiptToPrint,
    private val saveReceiptFileUseCase: SaveReceiptFileUseCase,
    private val reloadIncompletePaymentsUseCase: ReloadIncompletePaymentsUseCase
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _networkStatus = MutableLiveData(ConnectivityObserver.Status.Available)
    val networkStatus: LiveData<ConnectivityObserver.Status> = _networkStatus

    private var _businessBasics: BusinessBasicsLocal? = null

    private val _sales = MutableLiveData<List<SaleItem>>()
    val sales: LiveData<List<SaleItem>> = _sales

    private val _cashBalance = MutableLiveData<CashBalance>()
    val cashBalance: LiveData<CashBalance> = _cashBalance

    private val _salesBalance = MutableLiveData<SalesBalance>()
    val salesBalance: LiveData<SalesBalance> = _salesBalance

    private val _printable = MutableLiveData<ArrayList<Printable>>()
    val printable: LiveData<ArrayList<Printable>> = _printable

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class UiEvent {
        data class ShowMessage(val resId: Int? = null) : UiEvent()
        data class ShareReceipt(val receipt: SharedReceipt): UiEvent()
        data class SavedReceipt(val file: File): UiEvent()
    }

    private var syncDataJob: Job? = null
    private var loadDataJob: Job? = null
    private var loadSalesJob: Job? = null
    private var computeBalanceJob: Job? = null
    private var networkObserverJob: Job? = null
    private var reloadSalesJob: Job? = null
    private var loadReceiptToSendJob: Job? = null

    init {
        loadData()
        observeNetworkChange()
    }

    private fun observeNetworkChange() {
        networkObserverJob?.cancel()
        networkObserverJob = viewModelScope.launch(Dispatchers.IO) {
            (myApplication as MyApplication).networkStatus.collectLatest {
                _networkStatus.postValue(it)
                if (it == ConnectivityObserver.Status.Available)
                    if (isInternetAvailable()) {
                        syncDataFromLocalStore()
                    }
            }
        }

    }

    private fun loadData() {
        loadDataJob?.cancel()
        loadDataJob = viewModelScope.launch(Dispatchers.IO) {
            loadSales()
            computeBalance()
            loadBusinessBasics()
            delay(1000)
            syncDataFromLocalStore()
        }

    }

    private suspend fun loadBusinessBasics() {
        val businessBasics = businessBasicsRepository.getBusinessBasics().first()
        if (businessBasics is Resource.Success && businessBasics.data != null)
            _businessBasics = businessBasics.data
    }

    private fun fetchSalesFromNetwork() {
        reloadSalesJob?.cancel()
        reloadSalesJob = viewModelScope.launch(Dispatchers.IO) {
            reloadSalesUseCase(
                _businessBasics!!.id,
                _businessBasics!!.storeId,
                DateFormat.format(System.currentTimeMillis(), "yyyy-MM-dd"),
                _businessBasics!!.companyId
            )
        }
    }

    fun syncDataFromLocalStore() {
        if ((_salesBalance.value?.pendingToSend ?: 1) == 0) {
            _loading.postValue(false)
            return
        }
        syncDataJob?.cancel()
        syncDataJob = viewModelScope.launch(Dispatchers.IO) {
            if (!isInternetAvailable()) return@launch
            _loading.postValue(true)
            async {
                _businessBasics?.apply {
                    RemoteSaleSyncFromLocalUseCase.execute(
                        this.id,
                        this.sellerId,
                        this.storeId,
                        this.companyId
                    )
                    reloadIncompletePaymentsUseCase(
                        this.id,
                        this.storeId,
                        this.companyId
                    )

                }
            }.await()
            if (_salesBalance.value?.pendingToSend ?: 1 > 0) {
                _loading.postValue(false)
                _uiEvent.emit(UiEvent.ShowMessage(R.string.sync_error))
            } else
                _loading.postValue(false)

        }

    }

    fun loadSales(query: String = "") {
        loadSalesJob?.cancel()
        loadSalesJob = viewModelScope.launch(Dispatchers.IO) {
            getSalesOfDayUseCase(query).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            _sales.postValue(it)
                            if (it.isEmpty()) {
                                _uiEvent.emit(UiEvent.ShowMessage(R.string.without_sales))
                            }
                        }
                    }

                    else -> {
                    }
                }

            }.collect()
        }
    }

    private fun computeBalance() {
        computeBalanceJob?.cancel()
        computeBalanceJob = viewModelScope.launch(Dispatchers.IO) {
            async {
                GetCashBalanceUseCase().onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { _cashBalance.postValue(it) }
                        }
                        else -> {
                        }
                    }

                }.collect()
            }
            async {
                getSalesBalanceUseCase().onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { _salesBalance.postValue(it) }
                        }
                        else -> {
                        }
                    }

                }.collect()
            }
        }
    }

    fun loadReceiptToSend(saleId: Int) {
        _loading.postValue(true)
        loadReceiptToSendJob?.cancel()
        loadReceiptToSendJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = getReceiptToSend(saleId)) {
                is Resource.Success -> {
                    _loading.postValue(false)
                    result.data?.let { _uiEvent.emit(UiEvent.ShareReceipt(it)) }
                }
                is Resource.Error -> {
                    _loading.postValue(false)
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

    fun saveReceiptFile(file: File, note: String){
        _loading.postValue(true)
        loadReceiptToSendJob?.cancel()
        loadReceiptToSendJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = saveReceiptFileUseCase(file,note)) {
                is Resource.Success -> {
                    _loading.postValue(false)
                    result.data?.let { _uiEvent.emit(UiEvent.SavedReceipt(it)) }
                }
                is Resource.Error -> {
                    _loading.postValue(false)
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

    fun loadReceiptToPrint(saleId: Int) {
        _loading.postValue(true)
        loadReceiptToSendJob?.cancel()
        loadReceiptToSendJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = getReceiptToPrint(saleId)) {
                is Resource.Success -> {
                    _loading.postValue(false)
                    result.data?.let { _printable.postValue(it) }

                }
                is Resource.Error -> {
                    _loading.postValue(false)
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

    private suspend fun isInternetAvailable(): Boolean {
        if (_networkStatus.value != ConnectivityObserver.Status.Available) {
            _loading.postValue(false)
            _uiEvent.emit(UiEvent.ShowMessage(resId = R.string.internet_error))
            return false
        }
        return true
    }
}