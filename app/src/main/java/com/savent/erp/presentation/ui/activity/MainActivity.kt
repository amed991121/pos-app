package com.savent.erp.presentation.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.savent.erp.AppConstants
import com.savent.erp.R
import com.savent.erp.databinding.ActivityMainBinding
import com.savent.erp.domain.usecase.LocationRequestUseCase
import com.savent.erp.presentation.viewmodel.MainViewModel
import com.savent.erp.utils.CheckPermissions
import com.savent.erp.utils.IsLocationGranted
import com.savent.erp.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val locationRequestUseCase = LocationRequestUseCase()
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        subscribeToObservables()
    }

    private fun init() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = mainViewModel

        val destination = intent.getIntExtra("destination", R.id.createSaleFragment)
        mainViewModel.goToDestination(destination)
    }

    private fun subscribeToObservables() {
        mainViewModel.navActualDestination.observe(this) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(it)

        }
        mainViewModel.uiEvent.observe(this) { uiEvent ->
            when (uiEvent) {
                is MainViewModel.UiEvent.BackAction -> {
                    onBackPressed()
                }
                is MainViewModel.UiEvent.GoOn -> {
                    goOn()
                }
                else->{}
            }
        }
    }


    override fun onBackPressed() {
        when (mainViewModel.navActualDestination.value) {
            R.id.checkoutFragment -> {
                mainViewModel.goToDestination(R.id.addProductsFragment)
            }
            R.id.addProductsFragment -> {
                mainViewModel.goToDestination(R.id.createSaleFragment)
            }
            R.id.addClientFragment -> {
                if (mainViewModel.navPreviousDestination == R.id.createSaleFragment)
                    mainViewModel.goToDestination(R.id.createSaleFragment)
                else finish()
            }
            R.id.createSaleFragment -> {
                finish()
            }
        }
    }

    fun goOn() {
        when (mainViewModel.navActualDestination.value) {
            R.id.checkoutFragment -> {
                startActivity(Intent(this,LastSalesActivity::class.java))
                finish()
            }
            R.id.addProductsFragment -> {
                mainViewModel.goToDestination(R.id.checkoutFragment)
            }
            R.id.addClientFragment -> {
                if (mainViewModel.navPreviousDestination == R.id.createSaleFragment)
                    mainViewModel.goToDestination(R.id.createSaleFragment)
                else finish()
            }
            R.id.createSaleFragment -> {
                mainViewModel.goToDestination(R.id.addProductsFragment)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppConstants.REQUEST_LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && IsLocationGranted(this)) {
                lifecycleScope.launch {
                    locationRequestUseCase(this@MainActivity).collectLatest { result ->
                        when (result) {
                            is Resource.Success -> {
                                mainViewModel.requestLocationUpdates()
                            }
                            else -> {
                                return@collectLatest
                            }
                        }

                    }
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_LOCATION_CODE && resultCode == Activity.RESULT_OK){
            mainViewModel.runLocationUpdates()
        }
    }

}