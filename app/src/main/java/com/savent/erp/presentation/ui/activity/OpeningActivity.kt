package com.savent.erp.presentation.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.databinding.ActivityOpeningBinding
import com.savent.erp.presentation.viewmodel.OpeningViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class OpeningActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpeningBinding
    private val openingViewModel: OpeningViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        subscribeToObservables()
        checkIfLoggedIn()
    }


    private fun init() {
        binding = ActivityOpeningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = openingViewModel
    }

    private fun subscribeToObservables() {
        lifecycleScope.launchWhenStarted{
            delay(700)
            openingViewModel.uiEvent.observe(this@OpeningActivity) { uiEvent ->
                    when (uiEvent) {
                        is OpeningViewModel.UiEvent.LoggedIn -> {
                            val cls = if (uiEvent.success) DashboardActivity::class.java
                            else LoginActivity::class.java
                            startActivity(Intent(this@OpeningActivity, cls))
                            finish()
                        }
                        else -> {
                        }
                    }
                }
        }

    }

    private fun checkIfLoggedIn() {
        openingViewModel.isLogged()
    }

    override fun onStart() {
        super.onStart()

    }
}