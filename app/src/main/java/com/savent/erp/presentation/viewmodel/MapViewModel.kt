package com.savent.erp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savent.erp.R
import com.savent.erp.domain.usecase.GetMapClientItems
import com.savent.erp.presentation.ui.model.MapClientItem
import com.savent.erp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MapViewModel(private val getMapClientItems: GetMapClientItems): ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _clients = MutableLiveData<List<MapClientItem>>()
    val clients: LiveData<List<MapClientItem>> = _clients

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class UiEvent {
        data class ShowMessage(val resId: Int? = null, val message: String? = null) : UiEvent()
    }

    private var loadClientsJob: Job? = null


    fun loadClients() {
        loadClientsJob?.cancel()
        loadClientsJob = viewModelScope.launch(Dispatchers.IO) {
            getMapClientItems().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        result.data?.let { _clients.postValue(it) }
                        if (result.data?.isEmpty() == true)
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    R.string.get_map_client_items_empty
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
}