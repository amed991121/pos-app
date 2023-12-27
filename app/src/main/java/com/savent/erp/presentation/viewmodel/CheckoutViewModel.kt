package com.savent.erp.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mazenrashed.printooth.data.printable.Printable
import com.savent.erp.ConnectivityObserver
import com.savent.erp.MyApplication
import com.savent.erp.R
import com.savent.erp.data.local.model.BusinessBasicsLocal
import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.domain.usecase.*
import com.savent.erp.presentation.ui.model.Checkout
import com.savent.erp.presentation.ui.model.ProductItem
import com.savent.erp.presentation.ui.model.SharedReceipt
import com.savent.erp.utils.PaymentMethod
import com.savent.erp.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class CheckoutViewModel(
    private val myApplication: Application,
    private val businessBasicsRepository: BusinessBasicsRepository,
    private val getCheckoutSaleUseCase: GetCheckoutSaleUseCase,
    private val getSelectedProductsUseCase: GetSelectedProductsUseCase,
    private val saveCompletedSaleUseCase: SaveCompletedSaleUseCase,
    private val increaseExtraDiscountPercentUseCase: IncreaseExtraDiscountPercentUseCase,
    private val decreaseExtraDiscountPercentUseCase: DecreaseExtraDiscountPercentUseCase,
    private val updateExtraDiscountPercentUseCase: UpdateExtraDiscountPercentUseCase,
    private val updateCollectedPaymentUseCase: UpdateCollectedPaymentUseCase,
    private val updatePaymentMethodUseCase: UpdatePaymentMethodUseCase,
    private val verifyingRemainingCreditLimit: VerifyingRemainingCreditLimit,
    private val reloadIncompletePaymentsUseCase: ReloadIncompletePaymentsUseCase,
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _networkStatus = MutableLiveData(ConnectivityObserver.Status.Available)
    val networkStatus: LiveData<ConnectivityObserver.Status> = _networkStatus

    private val _saveSaleSuccess = MutableLiveData(false)
    val saveSaleSuccess: LiveData<Boolean> = _saveSaleSuccess

    private var _businessBasics: BusinessBasicsLocal? = null

    private val _checkout = MutableLiveData<Checkout>()
    val checkout: LiveData<Checkout> = _checkout

    private val _products = MutableLiveData<List<ProductItem>>()
    val products: LiveData<List<ProductItem>> = _products

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class UiEvent {
        data class ShowMessage(val resId: Int? = null, val message: String? = null) : UiEvent()
    }

    init {
        loadData()
        observeNetworkChange()
    }

    private var loadDataJob: Job? = null
    private var checkoutJob: Job? = null
    private var loadSelectedProducts: Job? = null
    private var updateExtraDiscountJob: Job? = null
    private var updatePaymentJob: Job? = null
    private var recordSaleJob: Job? = null
    private var networkObserverJob: Job? = null
    private var reloadIncompletePaymentsJob: Job? = null


    private fun observeNetworkChange() {
        networkObserverJob?.cancel()
        networkObserverJob = viewModelScope.launch(Dispatchers.IO) {
            (myApplication as MyApplication).networkStatus.collectLatest {
                _networkStatus.postValue(it)
                if (it == ConnectivityObserver.Status.Available)
                    if (isInternetAvailable()) {
                        fetchIncompletePaymentsFromNetwork()
                    }
            }
        }

    }

    private fun loadData() {
        loadDataJob?.cancel()
        loadDataJob = viewModelScope.launch(Dispatchers.IO) {
            loadCheckout()
            loadProducts()
            loadBusinessBasics()
            if (isInternetAvailable()) {
                fetchIncompletePaymentsFromNetwork()
            }
        }
    }

    private suspend fun loadBusinessBasics() {
        val businessBasics = businessBasicsRepository.getBusinessBasics().first()
        if (businessBasics is Resource.Success && businessBasics.data != null)
            _businessBasics = businessBasics.data
    }

    private fun fetchIncompletePaymentsFromNetwork() {
        reloadIncompletePaymentsJob?.cancel()
        reloadIncompletePaymentsJob = viewModelScope.launch(Dispatchers.IO) {
            reloadIncompletePaymentsUseCase(
                _businessBasics!!.id,
                _businessBasics!!.storeId,
                _businessBasics!!.companyId
            )
        }
    }

    private fun loadCheckout() {
        checkoutJob?.cancel()
        checkoutJob = viewModelScope.launch(Dispatchers.IO) {
            getCheckoutSaleUseCase().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _loading.postValue(true)
                    }
                    is Resource.Success -> {
                        _loading.postValue(false)
                        _checkout.postValue(result.data)
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

            }.collect()
        }
    }

    fun loadProducts() {
        loadSelectedProducts?.cancel()
        loadSelectedProducts = viewModelScope.launch(Dispatchers.IO) {
            getSelectedProductsUseCase().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        _products.postValue(result.data)
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

    fun increaseExtraDiscountPercent() {
        updateExtraDiscountJob?.cancel()
        updateExtraDiscountJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = increaseExtraDiscountPercentUseCase()) {
                is Resource.Error -> {
                    _uiEvent.emit(
                        UiEvent.ShowMessage(
                            result.resId ?: R.string.unknown_error
                        )
                    )
                }
                else -> {
                }
            }
        }
    }

    fun decreaseExtraDiscountPercent() {
        updateExtraDiscountJob?.cancel()
        updateExtraDiscountJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = decreaseExtraDiscountPercentUseCase()) {
                is Resource.Error -> {
                    _uiEvent.emit(
                        UiEvent.ShowMessage(
                            result.resId ?: R.string.unknown_error
                        )
                    )
                }
                else -> {
                }
            }
        }
    }

    fun updateExtraDiscountPayment(discount: Int) {
        updateExtraDiscountJob?.cancel()
        updateExtraDiscountJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = updateExtraDiscountPercentUseCase(discount)) {
                is Resource.Error -> {
                    _uiEvent.emit(
                        UiEvent.ShowMessage(
                            result.resId ?: R.string.unknown_error
                        )
                    )
                }
                else -> {
                }
            }
        }
    }

    fun updatePaymentMethod(method: PaymentMethod) {
        updatePaymentJob?.cancel()
        updatePaymentJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = updatePaymentMethodUseCase(method)) {
                is Resource.Error -> {
                    _uiEvent.emit(
                        UiEvent.ShowMessage(
                            result.resId ?: R.string.unknown_error
                        )
                    )
                }
                else -> {
                }
            }
        }
    }

    fun updateCollectedPayment(collected: Float) {
        updatePaymentJob?.cancel()
        updatePaymentJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = updateCollectedPaymentUseCase(collected)) {
                is Resource.Error -> {
                    _uiEvent.emit(
                        UiEvent.ShowMessage(
                            result.resId ?: R.string.unknown_error
                        )
                    )
                }
                else -> {
                }
            }
        }
    }

    fun recordSale() {
        recordSaleJob?.cancel()
        recordSaleJob = viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            when (val result = saveCompletedSaleUseCase()) {
                is Resource.Success -> {
                    coroutineScope {
                        _loading.postValue(true)
                        if (isInternetAvailable()){
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
                        }
                        _saveSaleSuccess.postValue(true)
                    }


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

    suspend fun proceedWithSale(): Boolean {
        _loading.postValue(true)
        return when (val result = verifyingRemainingCreditLimit()) {
            is Resource.Error -> {
                _loading.postValue(false)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        result.resId, result.message
                    )
                )
                false
            }
            else -> {
                _loading.postValue(false)
                true
            }
        }

    }

    private suspend fun isInternetAvailable(): Boolean {
        if (_networkStatus.value != ConnectivityObserver.Status.Available) {
            _uiEvent.emit(UiEvent.ShowMessage(resId = R.string.internet_error))
            return false
        }
        return true
    }


}