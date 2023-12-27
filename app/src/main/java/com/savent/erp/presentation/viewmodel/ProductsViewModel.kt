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
import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.domain.repository.ClientsRepository
import com.savent.erp.domain.repository.DiscountsRepository
import com.savent.erp.domain.usecase.*
import com.savent.erp.presentation.ui.model.ProductItem
import com.savent.erp.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ProductsViewModel(
    private val myApplication: Application,
    private val appPreferencesLocalDatasource: AppPreferencesLocalDatasource,
    private val businessBasicsRepository: BusinessBasicsRepository,
    private val getProductListUseCase: GetProductListUseCase,
    private val reloadProductsUseCase: ReloadProductsUseCase,
    private val removeAllProductsUseCase: RemoveAllProductsUseCase,
    private val removeProductFromSaleUseCase: RemoveProductFromSaleUseCase,
    private val addProductToSaleUseCase: AddProductToSaleUseCase,
    private val changeUnitsOfSelectedProductsUseCase: ChangeUnitsOfSelectedProductsUseCase,
    private val getPendingSaleUseCase: GetPendingSaleUseCase,
    private val clientsRepository: ClientsRepository,
    private val discountsRepository: DiscountsRepository
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _networkStatus = MutableLiveData(ConnectivityObserver.Status.Available)
    val networkStatus: LiveData<ConnectivityObserver.Status> = _networkStatus

    private val _appPreferences = MutableLiveData<AppPreferences>()
    val appPreferences: LiveData<AppPreferences> = _appPreferences

    private var _businessBasics: BusinessBasicsLocal? = null

    private val _products = MutableLiveData<List<ProductItem>>()
    val products: LiveData<List<ProductItem>> = _products

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class UiEvent {
        data class ShowMessage(val resId: Int? = null, val message: String? = null) : UiEvent()
        data class Continue(val available: Boolean = false) : UiEvent()
    }

    private var loadDataJob: Job? = null
    private var loadProductsJob: Job? = null
    private var reloadProductsJob: Job? = null
    private var changeProductsFilterJob: Job? = null
    private var syncIODataJob: Job? = null
    private var reloadDiscountsJob: Job? = null
    private var defaultJob: Job? = null
    private var addProductJob: Job? = null
    private var removeProductJob: Job? = null
    private var changeProductsUnits: Job? = null
    private var networkObserverJob: Job? = null

    init {
        observeNetworkChange()
        loadData()
    }

    private fun observeNetworkChange() {
        networkObserverJob?.cancel()
        networkObserverJob = viewModelScope.launch(Dispatchers.IO) {
            _networkStatus.postValue((myApplication as MyApplication).currentNetworkStatus)
            myApplication.networkStatus.collectLatest {
                _networkStatus.postValue(it)
                if (it == ConnectivityObserver.Status.Available)
                    syncIOData(
                        _appPreferences.value?.productsFilter ?: "",
                        _appPreferences.value?.loadProductsDiscounts ?: false
                    )
            }
        }

    }

    private fun loadData() {
        loadDataJob?.cancel()
        loadDataJob = viewModelScope.launch(Dispatchers.IO) {
            loadProducts()
            async { loadAppPreferences() }
            loadBusinessBasics()
            delay(1000)
            if (isInternetAvailable())
                syncIOData()
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

    fun changeProductsFilter(productsFilter: String, loadDiscounts: Boolean) {
        changeProductsFilterJob?.cancel()
        changeProductsFilterJob = viewModelScope.launch(Dispatchers.IO) {
            if (appPreferencesLocalDatasource.insertOrUpdateAppPreferences(
                    _appPreferences.value!!.copy(
                        productsFilter = productsFilter,
                        loadProductsDiscounts = loadDiscounts
                    )
                ) is Resource.Success
            )
                _appPreferences.postValue(_appPreferences.value!!.copy(productsFilter = productsFilter))

            if (isInternetAvailable()) {
                _loading.postValue(true)
                removeAllProductsUseCase()
                syncIOData(productsFilter, loadDiscounts)
            }

        }

    }

    private fun fetchProductsFromNetwork(productsFilter: String) {
        reloadProductsJob?.cancel()
        reloadProductsJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = reloadProductsUseCase(
                _businessBasics!!.storeId,
                _businessBasics?.companyId ?: 1,
                productsFilter
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

    private fun reloadClientDiscounts(loadDiscounts: Boolean) {
        reloadDiscountsJob?.cancel()
        reloadDiscountsJob = viewModelScope.launch(Dispatchers.IO) {
            val clientId = clientsRepository.getClient(
                getPendingSaleUseCase().first().data?.clientId ?: 1
            ).data?.remoteId ?: kotlin.run {
                _uiEvent.emit(
                    UiEvent.ShowMessage(R.string.get_clients_error)
                )
                return@launch
            }
            if (loadDiscounts) {
                when (val result = discountsRepository.fetchDiscounts(
                    _businessBasics?.storeId ?: 0,
                    clientId,
                )) {
                    is Resource.Error -> {
                        _uiEvent.emit(
                            UiEvent.ShowMessage(result.resId, result.message)
                        )

                    }
                    else -> {}
                }
            } else {
                when (val resource = discountsRepository.deleteDiscounts(clientId)) {
                    is Resource.Error -> {
                        _uiEvent.emit(
                            UiEvent.ShowMessage(resource.resId, resource.message)
                        )
                    }
                    else -> {}
                }
            }

        }
    }


    suspend fun syncIOData(
        productsFilter: String = _appPreferences.value?.productsFilter ?: "",
        loadDiscounts: Boolean = _appPreferences.value?.loadProductsDiscounts
            ?: false
    ) {
        _loading.postValue(true)
        syncIODataJob?.cancel()
        syncIODataJob = viewModelScope.launch(Dispatchers.IO) {
            reloadClientDiscounts(loadDiscounts)
            when (val result = RemoteSaleSyncFromLocalUseCase.execute(
                _businessBasics?.id ?: 0,
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
                    fetchProductsFromNetwork(
                        productsFilter
                    )
                }
                else -> {
                    _loading.postValue(false)
                }
            }
        }

    }


    fun loadProducts(query: String = "") {
        loadProductsJob?.cancel()
        loadProductsJob = viewModelScope.launch(Dispatchers.IO) {
            getProductListUseCase(query).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        result.data?.let { _products.postValue(it) }
                        if (result.data?.isEmpty() == true)
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    R.string.get_products_empty
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

    fun addProductToSale(productId: Int) {
        addProductJob?.cancel()
        addProductJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = addProductToSaleUseCase(productId)) {
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

    fun removeProductFromSale(productId: Int) {
        removeProductJob?.cancel()
        removeProductJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = removeProductFromSaleUseCase(productId)) {
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

    fun changeUnitsOfSelectedProducts(productId: Int, units: Int) {
        Log.d("log_","productId:$productId//units:$units")
        changeProductsUnits?.cancel()
        changeProductsUnits = viewModelScope.launch(Dispatchers.IO) {
            when (val result = changeUnitsOfSelectedProductsUseCase(productId, units)) {
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

    fun await() {
        defaultJob?.cancel()
        defaultJob = viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            if (!isAtLeastOneProductSelected()) return@launch
            addProductJob?.join()
            removeProductJob?.join()
            _loading.postValue(false)
            _uiEvent.emit(UiEvent.Continue(true))
        }
    }

    suspend fun isAtLeastOneProductSelected(): Boolean {
        val pendingSale = getPendingSaleUseCase().first()
        pendingSale.data?.let {
            if (it.selectedProducts.size < 1) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        R.string.one_product_at_least
                    )
                )
                return false
            }
        }
        return true
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