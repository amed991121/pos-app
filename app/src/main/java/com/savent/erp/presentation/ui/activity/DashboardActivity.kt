package com.savent.erp.presentation.ui.activity

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.savent.erp.R
import com.savent.erp.databinding.ActivityDashboardBinding
import com.savent.erp.presentation.ui.CustomSnackBar
import com.savent.erp.presentation.ui.dialog.ExitDialog
import com.savent.erp.presentation.ui.dialog.SyncDialog
import com.savent.erp.presentation.viewmodel.DashboardViewModel
import com.savent.erp.utils.PaymentMethod
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel


class DashboardActivity : AppCompatActivity(), ExitDialog.OnClickListener,
    SyncDialog.OnClickListener {
    private lateinit var binding: ActivityDashboardBinding
    private val dashboardViewModel by viewModel<DashboardViewModel>()
    private var defaultJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        subscribeToObservables()

    }

    private fun init() {
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = dashboardViewModel
        initEvents()
    }

    private fun initEvents() {

        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                if (!async { dashboardViewModel.isCreateClientAvailable() }.await())
                    withContext(Dispatchers.Main) { binding.addClient.visibility = View.GONE }
                if (async { dashboardViewModel.isShowRemainingProductsAvailable() }.await())
                    withContext(Dispatchers.Main) {
                        binding.getRemainingProducts.visibility = View.VISIBLE
                    }
            }

        }

        binding.addClient.setOnClickListener {
            goAddClient()
        }

        binding.createSale.setOnClickListener {
            goCreateSale()
        }

        binding.getDebts.setOnClickListener {
            getDebts()
        }

        binding.getDebtPayments.setOnClickListener {
            getDebtPayments()
        }

        binding.getRemainingProducts.setOnClickListener {
            getProducts()
        }
    }


    private fun subscribeToObservables() {

        dashboardViewModel.businessBasicsItem.observe(this) {
            it.image?.let {}
        }

        lifecycleScope.launchWhenCreated {
            dashboardViewModel.uiEvent.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { uiEvent ->
                    when (uiEvent) {
                        is DashboardViewModel.UiEvent.ShowMessage -> {
                            CustomSnackBar.make(
                                binding.root,
                                getString(uiEvent.resId ?: R.string.unknown_error),
                                CustomSnackBar.LENGTH_LONG
                            ).show()
                        }
                    }
                }

        }
    }

    fun seeSales(view: View) {
        startActivity(Intent(this, LastSalesActivity::class.java))
    }

    fun exitStore(view: View) {
        defaultJob?.cancel()
        defaultJob = lifecycleScope.launch(Dispatchers.IO) {
            if (async { dashboardViewModel.isDataPendingSync() }.await())
                withContext(Dispatchers.Main) {
                    val syncDialog = SyncDialog(this@DashboardActivity)
                    syncDialog.setOnClickListener(this@DashboardActivity)
                    syncDialog.show()
                }
            else
                withContext(Dispatchers.Main) {
                    val exitDialog = ExitDialog(this@DashboardActivity)
                    exitDialog.setOnClickListener(this@DashboardActivity)
                    exitDialog.show()
                }
        }

    }

    fun seeMovements(view: View) {
        startActivity(Intent(this, MovementActivity::class.java))
    }

    private fun goCreateSale() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("destination", R.id.createSaleFragment)
        startActivity(intent)
    }

    private fun goAddClient() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("destination", R.id.addClientFragment)
        startActivity(intent)
    }

    private fun getDebts() {
        startActivity(Intent(this, DebtsActivity::class.java))
    }

    private fun getDebtPayments() {
        startActivity(Intent(this, LastDebtPaymentsActivity::class.java))
    }

    private fun getProducts() {
        startActivity(Intent(this, RemainingProductsActivity::class.java))
    }

    override fun exit() {
        try {
            (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .clearApplicationUserData()
            startActivity(Intent(this, LoginActivity::class.java))
        } catch (e: Exception) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    override fun onClick() {
        dashboardViewModel.syncPendingData()
    }

}