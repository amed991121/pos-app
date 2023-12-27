package com.savent.erp.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.savent.erp.R
import com.savent.erp.data.local.model.CompanyEntity
import com.savent.erp.data.local.model.StoreEntity
import com.savent.erp.databinding.ActivityLoginBinding
import com.savent.erp.presentation.viewmodel.LoginViewModel
import com.savent.erp.data.remote.model.LoginCredentials
import com.savent.erp.presentation.ui.dialog.CompaniesDialog
import com.savent.erp.presentation.ui.dialog.StoresDialog
import com.savent.erp.utils.NameFormat
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : AppCompatActivity(){
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModel()
    private var companiesDialog: CompaniesDialog? = null
    private var storesDialog: StoresDialog? = null
    private val companiesListener = object: CompaniesDialog.OnEventListener{
        override fun onClick(company: CompanyEntity) {
            loginViewModel.setCompany(company)
        }

        override fun onSearch(query: String) {
            loginViewModel.loadCompanies(query)
        }

    }

    private val storesListener = object : StoresDialog.OnEventListener{
        override fun onClick(store: StoreEntity) {
            loginViewModel.setStore(store)
        }

        override fun onSearchStores(query: String) {
            loginViewModel.loadStores(query)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        subscribeToObservables()
    }

    private fun init() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = loginViewModel
        initEvents()

    }

    private fun subscribeToObservables() {

        loginViewModel.selectedCompany.observe(this){
            it?.let {
                binding.companyEdit.text = NameFormat.format(it.name)
            }
        }

        loginViewModel.selectedStore.observe(this){
            it?.let {
                binding.storeEdit.text = NameFormat.format(it.name)
            }
        }

        loginViewModel.companies.observe(this){
            companiesDialog?.setData(it)
        }

        loginViewModel.stores.observe(this){
            storesDialog?.setData(it)
        }

        loginViewModel.loginError.observe(this) { result ->
            binding.pinEdit.error = result.rfcError?.let { getString(it) }
            binding.pinEdit.error = result.pinError?.let { getString(it) }
            //binding.storeEdit.error = result.storeError?.let { getString(it) }
        }

        loginViewModel.loggedIn.observe(this) {
            if (it) {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        DashboardActivity::class.java
                    )
                )
                finish()
            }

        }

        lifecycleScope.launchWhenCreated {
            loginViewModel.uiEvent
                .collectLatest { uiEvent ->
                    when (uiEvent) {
                        is LoginViewModel.UiEvent.ShowMessage -> {
                            Toast.makeText(
                                this@LoginActivity,
                                uiEvent.resId?.let { getString(it) } ?: uiEvent.message
                                ?: getString(R.string.unknown_error),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

        }

    }


    fun login(view: View) {
        loginViewModel.login(
            LoginCredentials(
                binding.rfcEdit.text.toString().trim(),
                binding.pinEdit.text.toString().trim()
            )
        )

    }

    private fun initEvents() {

        binding.companyEdit.setOnClickListener {
            loginViewModel.loadCompanies()
            loginViewModel.reloadCompanies()
            companiesDialog = CompaniesDialog(this, loginViewModel.companies.value ?: listOf())
            companiesDialog?.setOnEventListener(companiesListener)
            companiesDialog?.show()
        }

        binding.storeEdit.setOnClickListener {
            loginViewModel.loadStores()
            loginViewModel.reloadStores()
            storesDialog = StoresDialog(this, loginViewModel.stores.value?: listOf())
            storesDialog?.setOnEventListener(storesListener)
            storesDialog?.show()
        }

    }


}