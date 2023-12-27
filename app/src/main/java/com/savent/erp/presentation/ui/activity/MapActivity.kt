package com.savent.erp.presentation.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.savent.erp.AppConstants
import com.savent.erp.R
import com.savent.erp.presentation.ui.CustomSnackBar
import com.savent.erp.presentation.ui.adapters.CustomBalloonViewAdapter
import com.savent.erp.presentation.ui.model.MapClientItem
import com.savent.erp.presentation.viewmodel.MapViewModel
import com.savent.erp.presentation.viewmodel.SalesViewModel
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.image.Image
import com.tomtom.sdk.map.display.image.ImageFactory
import com.tomtom.sdk.map.display.marker.Marker
import com.tomtom.sdk.map.display.marker.MarkerOptions
import com.tomtom.sdk.map.display.style.StandardStyles
import com.tomtom.sdk.map.display.ui.MapFragment
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapActivity : AppCompatActivity() {
    private val mapViewModel: MapViewModel by viewModel()
    private var tomTomMap: TomTomMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        initMap()
        subscribeToObservables()

    }

    private fun initMap() {
        val mapFragment = MapFragment.newInstance(MapOptions(AppConstants.MAPS_API_KEY))
        supportFragmentManager.beginTransaction().replace(R.id.map_container, mapFragment).commit()

        mapFragment.getMapAsync {
            //mapFragment.markerBalloonViewAdapter = CustomBalloonViewAdapter(this)
            tomTomMap = it
            mapViewModel.loadClients()
            findViewById<ProgressBar>(R.id.progress).visibility = View.GONE
        }
    }

    private fun subscribeToObservables() {
        mapViewModel.clients.observe(this) {
            addMapMarkers(it)
            addEvents()
        }
        lifecycleScope.launchWhenCreated {
            mapViewModel.uiEvent.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { uiEvent ->
                    when (uiEvent) {
                        is MapViewModel.UiEvent.ShowMessage -> {
                            CustomSnackBar.make(
                                findViewById(R.id.map_container),
                                getString(uiEvent.resId ?: R.string.unknown_error),
                                CustomSnackBar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
        }
    }

    private fun addMapMarkers(clientItems: List<MapClientItem>) {
        clientItems.forEach {
            val geoPoint = GeoPoint(it.latLng.latitude, it.latLng.longitude)
            val markerOptions = MarkerOptions(
                coordinate = geoPoint,
                pinImage = ImageFactory.fromResource(R.drawable.round_location_on_24),
                balloonText = it.name,
            )
            tomTomMap?.addMarker(markerOptions)
        }
        tomTomMap?.zoomToMarkers()
        tomTomMap?.moveCamera(CameraOptions(zoom = 11.0))

    }

    private fun addEvents() {
        tomTomMap?.addMarkerClickListener {
            tomTomMap?.moveCamera(CameraOptions(it.coordinate))
            if (!it.isSelected()) it.select()
        }
    }


}