package com.savent.erp.presentation.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.savent.erp.databinding.ActivityMovementBinding
import com.savent.erp.presentation.viewmodel.MovementsViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovementBinding
    private val movementsViewModel: MovementsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        subscribeToObservables()
    }

    private fun init() {
        binding = ActivityMovementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = movementsViewModel
    }

    private fun subscribeToObservables() {
        lifecycleScope.launchWhenCreated {
            movementsViewModel.uiEvent.collectLatest { uiEvent ->
                when (uiEvent) {
                    is MovementsViewModel.UiEvent.Back -> {
                        onBackPressed()
                    }
                    else -> {}
                }
            }
        }
    }
}