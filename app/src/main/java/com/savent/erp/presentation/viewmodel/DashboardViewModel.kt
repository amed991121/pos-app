package com.savent.erp.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savent.erp.ConnectivityObserver
import com.savent.erp.MyApplication
import com.savent.erp.R
import com.savent.erp.data.local.datasource.AppPreferencesLocalDatasource
import com.savent.erp.data.local.model.AppPreferences
import com.savent.erp.data.local.model.BusinessBasicsLocal
import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.domain.usecase.*
import com.savent.erp.presentation.ui.model.BusinessBasicsItem
import com.savent.erp.presentation.ui.model.StatItem
import com.savent.erp.utils.NameFormat
import com.savent.erp.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class DashboardViewModel(
    private val myApplication: Application,
    private val businessBasicsRepository: BusinessBasicsRepository,
    private val isDataPendingSyncUseCase: IsDataPendingSyncUseCase,
    private val remoteDataSyncFromLocalUseCase: RemoteDataSyncFromLocalUseCase,
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private var _networkStatus = ConnectivityObserver.Status.Available

    private var _businessBasics: BusinessBasicsLocal? = null

    private val _businessBasicsItem = MutableLiveData<BusinessBasicsItem>()
    val businessBasicsItem: LiveData<BusinessBasicsItem> = _businessBasicsItem

    private val _stats = MutableLiveData<List<StatItem>>()
    val stats: LiveData<List<StatItem>> = _stats

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class UiEvent {
        data class ShowMessage(val resId: Int? = null) : UiEvent()
    }

    private var networkObserverJob: Job? = null
    private var loadDataJob: Job? = null
    private var loadBusinessBasicsJob: Job? = null
    private var pendingSyncDataJob: Job? = null

    init {
        loadData()
        observeNetworkChange()
    }

    private fun observeNetworkChange() {
        networkObserverJob?.cancel()
        networkObserverJob = viewModelScope.launch(Dispatchers.IO) {
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

    private fun loadData() {
        loadDataJob?.cancel()
        loadDataJob = viewModelScope.launch(Dispatchers.IO) {
            loadBusinessBasicsUiItem()
            loadBusinessBasics()
        }

    }

    private suspend fun loadBusinessBasics() {
        val businessBasics = businessBasicsRepository.getBusinessBasics().first()
        if (businessBasics is Resource.Success && businessBasics.data != null)
            _businessBasics = businessBasics.data
    }

    suspend fun isCreateClientAvailable(): Boolean =
        businessBasicsRepository.getBusinessBasics().first().data?.sellerLevel!! > 2

    suspend fun isShowRemainingProductsAvailable(): Boolean =
        businessBasicsRepository.getBusinessBasics().first().data?.sellerLevel!! == 1

    suspend fun isInternetAvailable(): Boolean {
        if (_networkStatus != ConnectivityObserver.Status.Available) {
            _loading.postValue(false)
            _uiEvent.emit(UiEvent.ShowMessage(resId = R.string.internet_error))
            return false
        }
        return true
    }


    private fun loadBusinessBasicsUiItem() {
        loadBusinessBasicsJob?.cancel()
        loadBusinessBasicsJob = viewModelScope.launch(Dispatchers.IO) {
            businessBasicsRepository.getBusinessBasics().onEach {
                if (it is Resource.Success)
                    _businessBasicsItem.postValue(it.data?.let { it1 -> mapToUiItem(it1) })

            }.collect()
        }
    }

    private fun mapToUiItem(businessBasics: BusinessBasicsLocal): BusinessBasicsItem {
        return BusinessBasicsItem(
            businessBasics.id,
            NameFormat.format(businessBasics.name),
            NameFormat.format(businessBasics.storeName),
            businessBasics.image,
            NameFormat.format(businessBasics.address),
            businessBasics.receiptFooter
        )
    }

    suspend fun isDataPendingSync(): Boolean = isDataPendingSyncUseCase().first()

    fun syncPendingData() {
        _loading.postValue(true)
        pendingSyncDataJob?.cancel()
        pendingSyncDataJob = viewModelScope.launch(Dispatchers.IO) {
            if (!isInternetAvailable()) {
                _uiEvent.emit(UiEvent.ShowMessage(R.string.internet_error))
                return@launch
            }
            when (val result = remoteDataSyncFromLocalUseCase(
                _businessBasics?.id!!,
                _businessBasics?.sellerId!!,
                _businessBasics?.storeId!!,
                _businessBasics?.companyId!!
            ).first()
            ) {
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
    }


}