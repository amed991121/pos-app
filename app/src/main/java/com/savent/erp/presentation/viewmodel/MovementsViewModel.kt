package com.savent.erp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.savent.erp.ConnectivityObserver
import com.savent.erp.NetworkConnectivityObserver
import com.savent.erp.R
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.data.local.datasource.AppPreferencesLocalDatasource
import com.savent.erp.data.local.model.BusinessBasicsLocal
import com.savent.erp.data.local.model.StoreEntity
import com.savent.erp.data.remote.model.Movement
import com.savent.erp.domain.repository.*
import com.savent.erp.domain.usecase.*
import com.savent.erp.presentation.ui.model.*
import com.savent.erp.utils.DateTimeObj
import com.savent.erp.utils.NameFormat
import com.savent.erp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovementsViewModel(
    private val connectivityObserver: NetworkConnectivityObserver,
    private val businessBasicsRepository: BusinessBasicsRepository,
    private val movementsRepository: MovementsRepository,
    private val purchasesRepository: PurchasesRepository,
    private val getAvailablePurchasesUseCase: GetAvailablePurchasesUseCase,
    private val getMovementsOfDayUseCase: GetMovementsOfDayUseCase,
    private val saveMovementUseCase: SaveMovementUseCase,
    private val movementReasonsRepository: MovementReasonsRepository,
    private val getMovementReasonsUseCase: GetMovementReasonsUseCase,
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val storesRepository: StoresRepository,
    private val reloadEmployeesUseCase: ReloadEmployeesUseCase,
    private val reloadProvidersUseCase: ReloadProvidersUseCase,
    private val productsRepository: ProductsRepository,
    private val appPreferencesLocalDatasource: AppPreferencesLocalDatasource,
    private val getAvailableProductsFromPurchaseUseCase: GetAvailableProductsFromPurchaseUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private var _networkStatus = ConnectivityObserver.Status.Available

    private var _businessBasics: BusinessBasicsLocal? = null

    private var movement: Movement = Movement()

    private var movementItem =
        MovementItem(0, 0, MovementType.ALL, "", "", "", "", null, null, null, null, null, 0, true)
    private val _movementItem = MutableLiveData<MovementItem>()
    val movItem = _movementItem

    private var movementType = MovementType.ALL
    private var reasonType = MovementType.ALL
    private var queryReasons = ""
    private var productQuery = ""

    private val _isPendingSyncMovements = MutableLiveData(false)
    val isPendingSyncMovements: LiveData<Boolean> = _isPendingSyncMovements

    private val _movements = MutableLiveData<List<MovementItem>>()
    val movements: LiveData<List<MovementItem>> = _movements

    private val _reasons = MutableLiveData<List<MovementReasonItem>>()
    val reasons: LiveData<List<MovementReasonItem>> = _reasons

    private val _stores = MutableLiveData<List<StoreEntity>>()
    val stores: LiveData<List<StoreEntity>> = _stores

    private val _employees = MutableLiveData<List<EmployeeItem>>()
    val employees: LiveData<List<EmployeeItem>> = _employees

    private val _purchases = MutableLiveData<List<PurchaseItem>>()
    val purchases: LiveData<List<PurchaseItem>> = _purchases

    private val _products = MutableLiveData<List<ProductItem>>()
    val products: LiveData<List<ProductItem>> = _products

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var networkObserverJob: Job? = null
    private var loadBusinessBasicsJob: Job? = null
    private var loadMovementsJob: Job? = null
    private var saveMovementJob: Job? = null
    private var isPendingSyncMovementsJob: Job? = null
    private var syncMovementsJob: Job? = null
    private var loadPurchasesJob: Job? = null
    private var loadEmployeesJob: Job? = null
    private var loadStoresJob: Job? = null
    private var loadReasonsJob: Job? = null
    private var loadProductsJob: Job? = null
    private var reloadEmployeesJob: Job? = null
    private var reloadStoresJob: Job? = null
    private var reloadProvidersJob: Job? = null
    private var reloadReasonsJob: Job? = null
    private var reloadProductsJob: Job? = null
    private var defaultJob: Job? = null


    sealed class UiEvent {
        data class ShowMessage(val resId: Int? = null, val message: String? = null) : UiEvent()
        object MovementSaved : UiEvent()
        object Back : UiEvent()
    }

    enum class Type {
        INPUT, OUTPUT
    }

    init {
        loadBusinessBasics()
        observeNetworkChange()
        loadMovements()
        observePendingMovements()
    }


    private fun observeNetworkChange() {
        networkObserverJob?.cancel()
        networkObserverJob = viewModelScope.launch(Dispatchers.IO) {
            connectivityObserver.observe().collectLatest { status ->
                _networkStatus = status
                syncMovements()
            }
        }

    }

    private fun loadBusinessBasics() {
        loadBusinessBasicsJob?.cancel()
        loadBusinessBasicsJob = viewModelScope.launch(Dispatchers.IO) {
            val businessBasics = businessBasicsRepository.getBusinessBasics().first()
            if (businessBasics is Resource.Success && businessBasics.data != null)
                _businessBasics = businessBasics.data
        }
    }

    fun loadMovements(type: MovementType = movementType, query: String = "") {
        movementType = type
        loadMovementsJob?.cancel()
        loadMovementsJob = viewModelScope.launch(Dispatchers.IO) {
            getMovementsOfDayUseCase(type, query).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { _movements.postValue(it) }
                        /*if (resource.data?.isEmpty() == true)
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    R.string.get_movements_empty
                                )
                            )*/
                    }
                    is Resource.Error -> {
                        _uiEvent.emit(
                            UiEvent.ShowMessage(
                                resource.resId, resource.message
                            )
                        )

                    }
                    else -> {}
                }
            }
        }
    }

    fun saveMovement() {
        saveMovementJob?.cancel()
        saveMovementJob = viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            movement =
                movement.copy(
                    dateTimestamp = DateTimeObj.fromLong(
                        System.currentTimeMillis()
                    )
                )
            when (val result = saveMovementUseCase(movement)) {
                is Resource.Success -> {
                    _uiEvent.emit(UiEvent.MovementSaved)
                }
                is Resource.Error -> {
                    _uiEvent.emit(UiEvent.ShowMessage(result.resId, result.message))

                }
                else -> {}
            }
            _loading.postValue(false)
        }
    }

    fun syncMovements() {
        syncMovementsJob?.cancel()
        syncMovementsJob = viewModelScope.launch(Dispatchers.IO) {
            _businessBasics?.let {
                when (movementsRepository.syncOutputData(it.id, it.companyId)) {
                    is Resource.Success -> {
                        //Log.d("log_","syncMov Success")
                        if (purchasesRepository.fetchPurchases(it.companyId) is Resource.Error)
                            _uiEvent.emit(
                                UiEvent.ShowMessage(R.string.sync_purchases_error)
                            )
                        if(movementsRepository.fetchMovements(it.id,it.storeId,it.companyId) is Resource.Error)
                            _uiEvent.emit(
                                UiEvent.ShowMessage(R.string.update_movements_error)
                            )
                    }

                    is Resource.Error -> {
                        _uiEvent.emit(
                            UiEvent.ShowMessage(R.string.sync_movements_error)
                        )
                    }
                    else -> {}
                }
            }
            _loading.postValue(false)
        }
    }

    private fun observePendingMovements() {
        isPendingSyncMovementsJob?.cancel()
        isPendingSyncMovementsJob = viewModelScope.launch(Dispatchers.IO) {
            movementsRepository.isPendingOutputSync().collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _isPendingSyncMovements.postValue(resource.data)
                    }
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

    fun loadReasons(query: String = queryReasons, type: MovementType = reasonType) {
        queryReasons = query
        reasonType = type
        loadReasonsJob?.cancel()
        loadReasonsJob = viewModelScope.launch(Dispatchers.IO) {
            getMovementReasonsUseCase(query, type).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { _reasons.postValue(it) }
                    }
                    is Resource.Error -> {
                        _uiEvent.emit(UiEvent.ShowMessage(resId = resource.resId, resource.message))
                    }
                    else -> {}
                }
            }
        }
    }

    fun loadEmployees(query: String = "") {
        loadEmployeesJob?.cancel()
        loadEmployeesJob = viewModelScope.launch(Dispatchers.IO) {
            getEmployeesUseCase(query).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { _employees.postValue(it) }
                        if (resource.data?.isEmpty() == true)
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    R.string.get_employees_empty
                                )
                            )
                    }
                    is Resource.Error -> {
                        _uiEvent.emit(
                            UiEvent.ShowMessage(
                                resource.resId, resource.message
                            )
                        )

                    }
                    else -> {}
                }
            }
        }
    }

    fun loadStores(query: String = "") {
        loadStoresJob?.cancel()
        loadStoresJob = viewModelScope.launch(Dispatchers.IO) {
            _businessBasics?.let { business ->
                storesRepository.getStores(query, business.companyId).collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data?.let { _stores.postValue(it) }
                            if (resource.data?.isEmpty() == true)
                                _uiEvent.emit(
                                    UiEvent.ShowMessage(
                                        R.string.stores_empty
                                    )
                                )
                        }
                        is Resource.Error -> {
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    resource.resId, resource.message
                                )
                            )

                        }
                        else -> {}
                    }
                }
            }

        }
    }

    fun loadPurchases(query: String = "") {
        loadPurchasesJob?.cancel()
        loadPurchasesJob = viewModelScope.launch(Dispatchers.IO) {
            getAvailablePurchasesUseCase(query).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { _purchases.postValue(it) }
                        if (resource.data?.isEmpty() == true)
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    R.string.get_purchases_empty
                                )
                            )
                    }
                    is Resource.Error -> {
                        _uiEvent.emit(
                            UiEvent.ShowMessage(
                                resource.resId, resource.message
                            )
                        )

                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadProductsFromPurchase(purchaseId: Int, query: String = "") {
        loadProductsJob?.cancel()
        loadProductsJob = viewModelScope.launch(Dispatchers.IO) {
            productQuery = query
            getAvailableProductsFromPurchaseUseCase(
                purchaseId,
                query,
                movement.selectedProducts
            ).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { _products.postValue(it) }
                        if (resource.data?.isEmpty() == true)
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    R.string.get_products_empty
                                )
                            )
                    }
                    is Resource.Error -> {
                        _uiEvent.emit(
                            UiEvent.ShowMessage(
                                resource.resId, resource.message
                            )
                        )

                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadAllProducts(query: String = "") {
        loadProductsJob?.cancel()
        loadProductsJob = viewModelScope.launch(Dispatchers.IO) {
            productQuery = query
            getAllProductsUseCase(query, movement.selectedProducts).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { _products.postValue(it) }
                        if (resource.data?.isEmpty() == true)
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    R.string.get_products_empty
                                )
                            )
                    }
                    is Resource.Error -> {
                        _uiEvent.emit(
                            UiEvent.ShowMessage(
                                resource.resId, resource.message
                            )
                        )

                    }
                    else -> {}
                }
            }
        }
    }

    fun loadProducts(query: String = "") {
        movement.purchaseId?.let { loadProductsFromPurchase(it, query) }
            ?: run { loadAllProducts(query) }
    }

    fun addProductUnit(productId: Int) {
        changeProductsUnits(productId, (movement.selectedProducts[productId] ?: 0) + 1)
    }

    fun removeProductUnit(productId: Int) {
        changeProductsUnits(
            productId,
            ((movement.selectedProducts[productId] ?: 0) - 1).let { if (it == -1) 0 else it })
    }

    fun changeProductsUnits(productId: Int, units: Int) = synchronized(this) {
        val selectedProducts = movement.selectedProducts
        selectedProducts[productId] = units
        movement = movement.copy(
            selectedProducts = selectedProducts,
            units = selectedProducts.values.sum()
        )
        movementItem = movementItem.copy(
            productsUnits = selectedProducts.values.sum()
        )
        //Log.d("log_","products:${selectedProducts}//sum:${selectedProducts.values.sum()}")
        _movementItem.postValue(movementItem)
        loadProducts(productQuery)
    }

    fun changeMovementReason(reason: MovementReasonItem) {
        movement = movement.copy(purchaseId = null)
        movement = movement.copy(reasonId = reason.id)
        movement = movement.copy(type = reason.type)
        movement = movement.copy(selectedProducts = HashMap())
        movement = movement.copy(units = 0)
        movementItem = movementItem.copy(purchaseId = null)
        movementItem = movementItem.copy(reason = reason.name)
        movementItem = movementItem.copy(type = reason.type)
        movementItem = movementItem.copy(productsUnits = 0)
        _movementItem.postValue(movementItem)
    }

    fun attachPurchase(purchaseId: Int) {
        movement = movement.copy(purchaseId = purchaseId)
        movement = movement.copy(selectedProducts = HashMap())
        movement = movement.copy(units = 0)
        movementItem = movementItem.copy(purchaseId = purchaseId)
        movementItem = movementItem.copy(productsUnits = 0)
        _movementItem.postValue(movementItem)
    }

    fun changeStores(store: StoreEntity, type: Type) {
        if (type == Type.INPUT) {
            movement = movement.copy(inputStoreId = store.remoteId)
            movementItem = movementItem.copy(inputStore = NameFormat.format(store.name))
        } else {
            movement = movement.copy(outputStoreId = store.remoteId)
            movementItem = movementItem.copy(outputStore = NameFormat.format(store.name))
        }
        _movementItem.postValue(movementItem)
    }

    fun changeStoreKeepers(employee: EmployeeItem, type: Type) {
        if (type == Type.INPUT) {
            movement = movement.copy(inputStoreKeeperId = employee.id)
            movementItem = movementItem.copy(inputStoreKeeper = employee.name)
        }
        else {
            movement = movement.copy(outputStoreKeeperId = employee.id)
            movementItem = movementItem.copy(outputStoreKeeper = employee.name)
        }
        _movementItem.postValue(movementItem)
    }

    fun fetchReasons() {
        reloadReasonsJob?.cancel()
        reloadReasonsJob = viewModelScope.launch(Dispatchers.IO) {
            _businessBasics?.let {
                if (movementReasonsRepository.fetchReasons(it.companyId) is Resource.Error)
                    _uiEvent.emit(
                        UiEvent.ShowMessage(R.string.sync_reasons_error)
                    )
            }
        }
    }

    fun fetchEmployees() {
        reloadEmployeesJob?.cancel()
        reloadEmployeesJob = viewModelScope.launch(Dispatchers.IO) {
            _businessBasics?.let {
                if (reloadEmployeesUseCase(it.companyId) is Resource.Error)
                    _uiEvent.emit(
                        UiEvent.ShowMessage(R.string.sync_employees_error)
                    )
            }
        }
    }

    fun fetchProviders() {
        reloadProvidersJob?.cancel()
        reloadProvidersJob = viewModelScope.launch(Dispatchers.IO) {
            _businessBasics?.let {
                if (reloadProvidersUseCase(it.companyId) is Resource.Error)
                    _uiEvent.emit(
                        UiEvent.ShowMessage(R.string.sync_providers_error)
                    )
            }
        }
    }

    fun fetchStores() {
        reloadStoresJob?.cancel()
        reloadStoresJob = viewModelScope.launch(Dispatchers.IO) {
            _businessBasics?.let {
                if (storesRepository.fetchStores(it.companyId) is Resource.Error)
                    _uiEvent.emit(
                        UiEvent.ShowMessage(R.string.update_stores_error)
                    )
            }
        }
    }

    fun fetchAllProducts() {
        reloadProductsJob?.cancel()
        reloadProductsJob = viewModelScope.launch(Dispatchers.IO) {
            _businessBasics?.let {
                val productFilter = "Todos"
                appPreferencesLocalDatasource.getAppPreferences()
                    .first().data?.copy(productsFilter = productFilter)
                    ?.let { it1 -> appPreferencesLocalDatasource.insertOrUpdateAppPreferences(it1) }
                if (productsRepository.fetchProducts(
                        it.storeId,
                        it.companyId,
                        productFilter
                    ) is Resource.Error
                )
                    _uiEvent.emit(
                        UiEvent.ShowMessage(R.string.fetch_products_error)
                    )
            }
        }
    }

    fun executeNetworkOps(){
        defaultJob?.cancel()
        defaultJob = viewModelScope.launch(Dispatchers.IO) {
            if (!isInternetAvailable()) return@launch
            _loading.postValue(true)
            fetchReasons()
            fetchStores()
            fetchEmployees()
            fetchProviders()
            syncMovements()
            fetchAllProducts()
        }
    }

    suspend fun isInternetAvailable(): Boolean {
        if (_networkStatus != ConnectivityObserver.Status.Available) {
            _loading.postValue(false)
            _uiEvent.emit(UiEvent.ShowMessage(resId = R.string.internet_error))
            return false
        }
        return true
    }

    fun resetProductsSearch(){
        productQuery = ""
    }

    fun resetReasonSearch(){
        queryReasons = ""
        reasonType = MovementType.ALL
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.Back)
        }
    }
}