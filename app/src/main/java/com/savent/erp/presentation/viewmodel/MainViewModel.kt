package com.savent.erp.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*

import com.google.android.gms.maps.model.LatLng
import com.savent.erp.ConnectivityObserver
import com.savent.erp.MyApplication
import com.savent.erp.R
import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.domain.usecase.ReloadClientsUseCase
import com.savent.erp.domain.usecase.ReloadProductsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(myApplication: Application) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private var locationManager: LocationManager? = null
    private val _latLng = MutableLiveData<LatLng>()
    val latLng: LiveData<LatLng> = _latLng

    private val _networkStatus = MutableLiveData(ConnectivityObserver.Status.Available)
    val networkStatus: LiveData<ConnectivityObserver.Status> = _networkStatus

    private val _navActualDestination = MutableLiveData<Int>(-1)
    val navActualDestination: LiveData<Int> = _navActualDestination

    var navPreviousDestination = -1

    private val _uiEvent = MutableLiveData<UiEvent>()
    val uiEvent: LiveData<UiEvent> = _uiEvent

    private val _message = MutableSharedFlow<UiEvent.ShowMessage>()
    val message = _message.asSharedFlow()

    sealed class UiEvent {
        data class ShowMessage(val resId: Int? = null) : UiEvent()
        data class BackAction(val id: Int? = null) : UiEvent()
        data class GoOn(val id: Int? = null) : UiEvent()
    }

    private var defaultJob: Job? = null
    private var locationUpdatesJob: Job? = null

    init {
        locationManager =
            myApplication.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }


    fun goToDestination(resId: Int) {
        _navActualDestination.value?.let { navPreviousDestination = it }
        _navActualDestination.value = resId
    }

    fun goOn() {
        defaultJob?.cancel()
        defaultJob = viewModelScope.launch(Dispatchers.IO) {
            _uiEvent.postValue(UiEvent.GoOn())
        }
    }

    fun back() {
        defaultJob?.cancel()
        defaultJob = viewModelScope.launch(Dispatchers.IO) {
            _uiEvent.postValue(UiEvent.BackAction())
        }
    }

    fun requestLocationUpdates() {
        _loading.postValue(true)
        var isGpsProviderAvailable = false
        var isNetworkProviderAvailable = false
        if (locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true) {
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000, 0f, locationListenerNetwork
            )
            isNetworkProviderAvailable = true
        }
        if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 0f, locationListenerGps
            )
            isGpsProviderAvailable = true
        }
        if (!isGpsProviderAvailable && !isNetworkProviderAvailable) {
            _loading.postValue(false)
            _uiEvent.postValue(UiEvent.ShowMessage(resId = R.string.location_error))
        }

    }

    fun removeLocationUpdates() {
        locationManager?.removeUpdates(locationListenerGps)
        locationManager?.removeUpdates(locationListenerNetwork)
        _loading.postValue(false)
    }

    fun runLocationUpdates() {
        locationUpdatesJob?.cancel()
        removeLocationUpdates()
        locationUpdatesJob = viewModelScope.launch {
            requestLocationUpdates()
        }
    }

    private val locationListenerNetwork: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            _latLng.postValue(LatLng(location.latitude, location.longitude))
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
        override fun onProviderEnabled(s: String) {}
        override fun onProviderDisabled(s: String) {}
    }

    private val locationListenerGps: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            _latLng.postValue(LatLng(location.latitude, location.longitude))
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
        override fun onProviderEnabled(s: String) {}
        override fun onProviderDisabled(s: String) {}
    }

}