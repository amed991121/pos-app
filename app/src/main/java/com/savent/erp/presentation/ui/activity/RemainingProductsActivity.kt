package com.savent.erp.presentation.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.savent.erp.AppConstants
import com.savent.erp.R
import com.savent.erp.databinding.ActivityRemainingProductsBinding
import com.savent.erp.presentation.ui.CustomSnackBar
import com.savent.erp.presentation.ui.adapters.RemainingProductsAdapter
import com.savent.erp.presentation.viewmodel.ProductsViewModel
import com.savent.erp.utils.CheckPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RemainingProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRemainingProductsBinding
    private val productsViewModel: ProductsViewModel by viewModel()
    private lateinit var productsAdapter: RemainingProductsAdapter
    private var defaultJob: Job? = null
    private val REQUEST_CODE = 0
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remaining_products)
        init()
        subscribeToObservables()
        setupRecyclerView()
    }

    private fun init() {
        binding = ActivityRemainingProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = productsViewModel
        initEvents()
    }

    private fun setupRecyclerView() {
        productsAdapter = RemainingProductsAdapter(this)
        val recyclerView = binding.productsRecycler
        recyclerView.layoutManager = LinearLayoutManager(this)
        productsAdapter.setHasStableIds(true)
        recyclerView.adapter = productsAdapter
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun subscribeToObservables() {
        productsViewModel.products.observe(this) {
            productsAdapter.setData(it)
        }

        productsViewModel.loading.observe(this){
            binding.refreshLayout.isRefreshing = it
        }

        lifecycleScope.launchWhenCreated {
            productsViewModel.uiEvent.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { uiEvent ->
                    when (uiEvent) {
                        is ProductsViewModel.UiEvent.ShowMessage -> {
                            CustomSnackBar.make(
                                binding.root,
                                uiEvent.resId?.let { getString(it) } ?: uiEvent.message
                                ?: getString(R.string.unknown_error),
                                CustomSnackBar.LENGTH_LONG
                            ).show()
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun initEvents() {

        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let { productsViewModel.loadProducts(it.toString()) }
            }

        })

        binding.refreshLayout.setOnRefreshListener {
            defaultJob?.cancel()
            defaultJob = lifecycleScope.launch(Dispatchers.IO) {
                if (productsViewModel.isInternetAvailable())
                    productsViewModel.syncIOData()
            }

        }

        binding.codeScanner.setOnClickListener {
            if (!CheckPermissions.check(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    REQUEST_CODE
                )
                return@setOnClickListener
            }
            val intent = Intent(this, ScannerActivity::class.java)
            startActivityForResult(intent, AppConstants.CODE_SCANNER)
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.CODE_SCANNER && resultCode == Activity.RESULT_OK) {
            binding.searchEdit.setText(data?.getStringExtra(AppConstants.RESULT_CODE_SCANNER))
            binding.searchMotion.transitionToState(binding.screenTitle.id)
        }
    }
}