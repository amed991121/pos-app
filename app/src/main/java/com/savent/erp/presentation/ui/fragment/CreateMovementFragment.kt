package com.savent.erp.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.savent.erp.R
import com.savent.erp.data.common.model.MovementReason
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.data.local.model.StoreEntity
import com.savent.erp.databinding.FragmentCreateMovementBinding
import com.savent.erp.presentation.ui.CustomSnackBar
import com.savent.erp.presentation.ui.activity.MainActivity
import com.savent.erp.presentation.ui.dialog.*
import com.savent.erp.presentation.ui.model.EmployeeItem
import com.savent.erp.presentation.ui.model.MovementReasonItem
import com.savent.erp.presentation.ui.model.PurchaseItem
import com.savent.erp.presentation.viewmodel.MovementsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateMovementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateMovementFragment : Fragment(), ReasonsDialog.OnEventListener,
    ProductsDialog.OnEventListener, PurchasesDialog.OnEventListener, StoresDialog.OnEventListener,
    EmployeesDialog.OnEventListener {
    private lateinit var binding: FragmentCreateMovementBinding
    private val movementsViewModel: MovementsViewModel by sharedViewModel()
    private var reasonsDialog: ReasonsDialog? = null
    private var purchasesDialog: PurchasesDialog? = null
    private var productsDialog: ProductsDialog? = null
    private var storesDialog: StoresDialog? = null
    private var employeesDialog: EmployeesDialog? = null
    private var selectedType: MovementsViewModel.Type = MovementsViewModel.Type.INPUT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeToObservables()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_create_movement,
            container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = movementsViewModel

        initEvents()

        return binding.root
    }

    private fun initEvents() {

        binding.reason.setOnClickListener {
            movementsViewModel.fetchReasons()
            movementsViewModel.loadReasons()
            reasonsDialog =
                ReasonsDialog(requireContext(), movementsViewModel.reasons.value ?: listOf())
            reasonsDialog?.setOnEventListener(this)
            reasonsDialog?.show()
        }

        binding.purchase.setOnClickListener {
            movementsViewModel.loadPurchases()
            purchasesDialog =
                PurchasesDialog(requireContext(), movementsViewModel.purchases.value ?: listOf())
            purchasesDialog?.setOnEventListener(this)
            purchasesDialog?.show()
        }

        binding.inputStore.setOnClickListener {
            movementsViewModel.fetchStores()
            movementsViewModel.loadStores()
            selectedType = MovementsViewModel.Type.INPUT
            storesDialog =
                StoresDialog(requireContext(), movementsViewModel.stores.value ?: listOf())
            storesDialog?.setOnEventListener(this)
            storesDialog?.show()
        }

        binding.outputStore.setOnClickListener {
            movementsViewModel.fetchStores()
            movementsViewModel.loadStores()
            selectedType = MovementsViewModel.Type.OUTPUT
            storesDialog =
                StoresDialog(requireContext(), movementsViewModel.stores.value ?: listOf())
            storesDialog?.setOnEventListener(this)
            storesDialog?.show()
        }

        binding.inputStoreKeeper.setOnClickListener {
            movementsViewModel.fetchEmployees()
            movementsViewModel.loadEmployees()
            selectedType = MovementsViewModel.Type.INPUT
            employeesDialog =
                EmployeesDialog(requireContext(), movementsViewModel.employees.value ?: listOf())
            employeesDialog?.setOnEventListener(this)
            employeesDialog?.show()
        }

        binding.outputStoreKeeper.setOnClickListener {
            movementsViewModel.fetchEmployees()
            movementsViewModel.loadEmployees()
            selectedType = MovementsViewModel.Type.OUTPUT
            employeesDialog =
                EmployeesDialog(requireContext(), movementsViewModel.employees.value ?: listOf())
            employeesDialog?.setOnEventListener(this)
            employeesDialog?.show()
        }

        binding.products.setOnClickListener {
            movementsViewModel.fetchAllProducts()
            movementsViewModel.loadProducts()
            productsDialog =
                ProductsDialog(requireContext(), movementsViewModel.products.value ?: listOf())
            productsDialog?.setOnEventListener(this)
            productsDialog?.show()
        }

        binding.save.setOnClickListener {
            binding.save.isEnabled = false
            movementsViewModel.saveMovement()
        }

        binding.back.setOnClickListener {
            movementsViewModel.onBackPressed()
        }
    }

    private fun subscribeToObservables() {

        movementsViewModel.movItem.observe(this) {

            if (it.reason.isNotEmpty()) {
                binding.reasonTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.reasonTv.text = it.reason

                when (it.type) {
                    MovementType.INPUT -> {
                        binding.inputStore.visibility = View.VISIBLE
                        binding.inputStoreKeeper.visibility = View.VISIBLE
                        binding.outputStore.visibility = View.GONE
                        binding.outputStoreKeeper.visibility = View.GONE
                    }
                    MovementType.OUTPUT -> {
                        binding.inputStore.visibility = View.GONE
                        binding.inputStoreKeeper.visibility = View.GONE
                        binding.outputStore.visibility = View.VISIBLE
                        binding.outputStoreKeeper.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.inputStore.visibility = View.VISIBLE
                        binding.inputStoreKeeper.visibility = View.VISIBLE
                        binding.outputStore.visibility = View.VISIBLE
                        binding.outputStoreKeeper.visibility = View.VISIBLE
                    }
                }
            }

            if (it.reason.contains(getString(R.string.purchase), true))
                binding.purchase.visibility = View.VISIBLE
            else
                binding.purchase.visibility = View.GONE

            if (it.purchaseId != null) {
                binding.purchaseTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.purchaseTv.text = "${getString(R.string.purchase_id)} ${it.purchaseId}"
            } else {
                binding.purchaseTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_150
                    )
                )
                binding.purchaseTv.text = getString(R.string.purchase)
            }

            if (it.inputStore != null) {
                binding.inputStoreTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.inputStoreTv.text = "${getString(R.string.input)}: ${it.inputStore}"
            }

            if (it.inputStoreKeeper != null) {
                binding.inputStoreKeeperTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.inputStoreKeeperTv.text =
                    "${getString(R.string.input)}: ${it.inputStoreKeeper}"
            }

            if (it.outputStore != null) {
                binding.outputStoreTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.outputStoreTv.text = "${getString(R.string.output)}: ${it.outputStore}"
            }

            if (it.outputStoreKeeper != null) {
                binding.outputStoreKeeperTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.outputStoreKeeperTv.text =
                    "${getString(R.string.output)}: ${it.outputStoreKeeper}"
            }

            if (it.productsUnits > 0) {
                binding.productsTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.productsTv.text = getString(R.string.units).format(it.productsUnits)
            } else {
                binding.productsTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_150
                    )
                )
                binding.productsTv.text = getString(R.string.products)
            }
        }

        movementsViewModel.reasons.observe(this) {
            reasonsDialog?.setData(it)
        }

        movementsViewModel.employees.observe(this) {
            employeesDialog?.setData(it)
        }

        movementsViewModel.purchases.observe(this) {
            purchasesDialog?.setData(it)
        }

        movementsViewModel.products.observe(this) {
            productsDialog?.setData(it)
        }

        movementsViewModel.stores.observe(this) {
            storesDialog?.setData(it)
        }

        lifecycleScope.launchWhenCreated {
            movementsViewModel.uiEvent.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { uiEvent ->
                    when (uiEvent) {
                        is MovementsViewModel.UiEvent.ShowMessage -> {
                            binding.save.isEnabled = true
                            try {
                                CustomSnackBar.make(
                                    binding.root,
                                    getString(uiEvent.resId ?: R.string.unknown_error),
                                    CustomSnackBar.LENGTH_LONG
                                ).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        is MovementsViewModel.UiEvent.MovementSaved -> {
                            binding.save.isEnabled = true
                            movementsViewModel.onBackPressed()
                        }
                        else -> {}
                    }
                }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateMovementFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateMovementFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(reason: MovementReasonItem) {
        if (reason.name.contains("Venta", true)) {
            activity?.startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
        movementsViewModel.changeMovementReason(reason)
    }

    override fun onSearchReasons(query: String) {
        movementsViewModel.loadReasons(query)
    }

    override fun onSetTypeFilter(type: MovementType) {
        movementsViewModel.loadReasons(type = type)
    }

    override fun reasonsClosed() {
        movementsViewModel.resetReasonSearch()
    }

    override fun onClick(purchase: PurchaseItem) {
        movementsViewModel.attachPurchase(purchase.id)
    }

    override fun onSearchPurchases(query: String) {
        movementsViewModel.loadPurchases(query)
    }

    override fun onClick(store: StoreEntity) {
        movementsViewModel.changeStores(store, selectedType)
    }

    override fun onSearchStores(query: String) {
        movementsViewModel.loadStores(query)
    }

    override fun onClick(employee: EmployeeItem) {
        movementsViewModel.changeStoreKeepers(employee, selectedType)
    }

    override fun onSearchEmployees(query: String) {
        movementsViewModel.loadEmployees(query)
    }

    override fun onSearchProducts(query: String) {
        movementsViewModel.loadProducts(query)
    }

    override fun addProduct(id: Int) {
        movementsViewModel.addProductUnit(id)
    }

    override fun removeProduct(id: Int) {
        movementsViewModel.removeProductUnit(id)
    }

    override fun changeProductUnits(productId: Int, units: Int) {
        movementsViewModel.changeProductsUnits(productId, units)
    }

    override fun goOn() {
        productsDialog?.dismiss()
    }

    override fun productsClosed() {
        movementsViewModel.resetProductsSearch()
    }

}