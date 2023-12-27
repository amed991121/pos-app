package com.savent.erp.presentation.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.savent.erp.R
import com.savent.erp.databinding.ActivityDebtsBinding
import com.savent.erp.presentation.ui.CustomSnackBar
import com.savent.erp.presentation.viewmodel.DebtsViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class DebtsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDebtsBinding
    private val debtsViewModel: DebtsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        subscribeToObservables()
    }

    private fun init() {
        binding = ActivityDebtsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = debtsViewModel
    }

    private fun subscribeToObservables() {
        lifecycleScope.launchWhenCreated {
            debtsViewModel.uiEvent.collectLatest { uiEvent ->
                when (uiEvent) {
                    is DebtsViewModel.UiEvent.Back-> {
                        onBackPressed()
                    }
                    else -> {}
                }
            }
        }
    }
}