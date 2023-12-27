package com.savent.erp.presentation.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.savent.erp.R
import com.savent.erp.databinding.ActivityLastDebtPaymentsBinding
import com.savent.erp.presentation.ui.CustomSnackBar
import com.savent.erp.presentation.ui.adapters.DebtPaymentsAdapter
import com.savent.erp.presentation.viewmodel.DebtsViewModel
import com.savent.erp.utils.DecimalFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class LastDebtPaymentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLastDebtPaymentsBinding
    private val debtsViewModel: DebtsViewModel by viewModel()
    private lateinit var debtsPaymentsAdapter: DebtPaymentsAdapter
    private var defaultJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        subscribeToObservables()
        setupRecyclerView()
    }

    private fun init() {
        binding = ActivityLastDebtPaymentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = debtsViewModel
        initEvents()
    }

    private fun setupRecyclerView() {
        debtsPaymentsAdapter = DebtPaymentsAdapter(this)
        val recyclerView = binding.debtPaymentsRecycler
        recyclerView.layoutManager = LinearLayoutManager(this)
        debtsPaymentsAdapter.setHasStableIds(true)
        recyclerView.adapter = debtsPaymentsAdapter
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun initEvents() {
        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let { debtsViewModel.loadDebtPayments(it.toString()) }
            }

        })
        binding.syncDebtPayments.setOnClickListener {
            defaultJob?.cancel()
            defaultJob = lifecycleScope.launch(Dispatchers.IO) {
                if (debtsViewModel.isInternetAvailable())
                    debtsViewModel.syncDebtIOData()
            }
        }

        binding.refreshLayout.setOnRefreshListener {
            defaultJob?.cancel()
            defaultJob = lifecycleScope.launch(Dispatchers.IO) {
                if (debtsViewModel.isInternetAvailable())
                    debtsViewModel.syncDebtIOData()
            }

        }


    }

    private fun subscribeToObservables() {
        debtsViewModel.debtPayments.observe(this) {
            debtsPaymentsAdapter.setData(it)
        }
        debtsViewModel.debtPaymentsBalance.observe(this) {

            binding.revenue.text = getString(R.string.price)
                .format(DecimalFormat.format(it.totalCollected))

            binding.sendingPending.text = it.pendingToSend.toString()

            binding.totalDebtPayments.text = it.total.toString()

            if (it.pendingToSend > 0) binding.syncDebtPayments.visibility = View.VISIBLE
            else binding.syncDebtPayments.visibility = View.GONE

        }

        debtsViewModel.loading.observe(this){
            binding.refreshLayout.isRefreshing = it
        }

        lifecycleScope.launchWhenCreated {
            debtsViewModel.loadDebtPayments()
        }

        lifecycleScope.launchWhenCreated {
            debtsViewModel.uiEvent.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { uiEvent ->
                    when (uiEvent) {
                        is DebtsViewModel.UiEvent.ShowMessage -> {
                            CustomSnackBar.make(
                                binding.root,
                                getString(uiEvent.resId ?: R.string.unknown_error),
                                CustomSnackBar.LENGTH_LONG
                            ).show()
                        }
                        else -> {}
                    }
                }
        }
    }

    fun back(view: View) {
        finish()
    }

}