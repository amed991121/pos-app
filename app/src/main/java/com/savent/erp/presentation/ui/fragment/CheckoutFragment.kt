package com.savent.erp.presentation.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.savent.erp.R
import com.savent.erp.databinding.FragmentCheckoutBinding
import com.savent.erp.presentation.ui.dialog.ConfirmSaleDialog
import com.savent.erp.presentation.ui.dialog.PaymentMethodDialog
import com.savent.erp.presentation.ui.adapters.ProductsAdapter
import com.savent.erp.presentation.viewmodel.CheckoutViewModel
import com.savent.erp.presentation.viewmodel.MainViewModel
import com.savent.erp.presentation.viewmodel.ProductsViewModel
import com.savent.erp.utils.DecimalFormat
import com.savent.erp.utils.PaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CheckoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckoutFragment : Fragment(), ProductsAdapter.OnEventListener,
    PaymentMethodDialog.OnClickListener, ConfirmSaleDialog.OnClickListener {

    private lateinit var binding: FragmentCheckoutBinding
    private val mainViewModel: MainViewModel by sharedViewModel()
    private val productsViewModel: ProductsViewModel by sharedViewModel()
    private val checkoutViewModel: CheckoutViewModel by viewModel()
    private lateinit var productsAdapter: ProductsAdapter
    private var defaultJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeToObservables()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_checkout,
            container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = checkoutViewModel

        initEvents()
        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        productsAdapter = ProductsAdapter(context)
        productsAdapter.setOnEventListener(this)
        val recyclerView = binding.productsRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        productsAdapter.setHasStableIds(true)
        recyclerView.adapter = productsAdapter
    }

    private fun subscribeToObservables() {
        checkoutViewModel.checkout.observe(this) {

            binding.subtotal.text = getString(R.string.price)
                .format(DecimalFormat.format(it.subtotal))

            binding.tax.text = getString(R.string.price)
                .format(DecimalFormat.format(it.taxes))

            binding.discounts.text = getString(R.string.price)
                .format(DecimalFormat.format(it.totalDiscounts))

            binding.total.text = getString(R.string.price)
                .format(DecimalFormat.format(it.finalPrice))

            binding.toPay.text = getString(R.string.price)
                .format(DecimalFormat.format(it.finalPrice))

            if (it.finalPrice < it.collected) {
                binding.change.text = getString(R.string.price)
                    .format(DecimalFormat.format(it.collected - it.finalPrice))
            } else
                binding.change.text = getString(R.string.price)
                    .format("0.00")

        }

        checkoutViewModel.products.observe(this) {
            productsAdapter.setData(it)
        }

        checkoutViewModel.saveSaleSuccess.observe(this) {
            if (it) mainViewModel.goOn()
        }

        lifecycleScope.launchWhenCreated {
            checkoutViewModel.uiEvent.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { uiEvent ->
                    when (uiEvent) {
                        is CheckoutViewModel.UiEvent.ShowMessage -> {
                            binding.goOn.isEnabled = true
                            Toast.makeText(
                                context,
                                uiEvent.resId?.let { getString(uiEvent.resId) } ?: uiEvent.message
                                ?: getString(R.string.unknown_error),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
        }
    }

    private fun initEvents() {
        binding.goOn.setOnClickListener {
            defaultJob?.cancel()
            defaultJob = lifecycleScope.launch(Dispatchers.IO) {
                if (productsViewModel.isAtLeastOneProductSelected()
                    && checkoutViewModel.proceedWithSale()
                ) {
                    withContext(Dispatchers.Main) {
                        binding.goOn.isEnabled = false
                        val confirmDialog = ConfirmSaleDialog(context!!)
                        confirmDialog.setOnClickListener(this@CheckoutFragment)
                        confirmDialog.show()
                    }
                }
            }
        }

        binding.openList.setOnClickListener {
            if (binding.productsRecycler.visibility == View.GONE) {
                binding.openList.text = getString(R.string.hide_list)
                binding.productsRecycler.visibility = View.VISIBLE
            } else {
                binding.openList.text = getString(R.string.see_list)
                binding.productsRecycler.visibility = View.GONE
            }
        }

        binding.addButton.setOnClickListener {
            checkoutViewModel.increaseExtraDiscountPercent()
        }

        binding.removeButton.setOnClickListener {
            checkoutViewModel.decreaseExtraDiscountPercent()
        }

        binding.discountPercent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    binding.discountPercent.setText("0")
                    binding.discountPercent.setSelection(binding.discountPercent.text.length)
                    return
                }

                checkoutViewModel.updateExtraDiscountPayment(s.toString().toInt())
            }

        })

        binding.paymentContainer.setOnClickListener {
            val paymentDialog = PaymentMethodDialog(context!!)
            paymentDialog.setOnClickListener(this)
            paymentDialog.show()
        }



        binding.collected.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty() || (s.length == 1 && s[0] == '.')) {
                    binding.collected.setText("0")
                    binding.collected.setSelection(binding.collected.text.length)
                    return
                }
                val strCollected = s.toString()
                if (strCollected[0] == '0' && strCollected.length == 2
                    && strCollected[1].isDigit()
                ) {
                    binding.collected.setText(strCollected.substring(1))
                    binding.collected.setSelection(binding.collected.text.length)
                    return
                }

                checkoutViewModel.updateCollectedPayment(
                    strCollected.replace(",", "").toFloat()
                )
            }

        })

        binding.backButton.setOnClickListener {
            mainViewModel.back()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CheckoutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onAddProductClick(id: Int) {
        productsViewModel.addProductToSale(id)
    }

    override fun onRemoveProductClick(id: Int) {
        productsViewModel.removeProductFromSale(id)
    }

    override fun onChangeProductsUnitsClick(id: Int, units: Int) {
        productsViewModel.changeUnitsOfSelectedProducts(id, units)
    }

    override fun onClick(method: PaymentMethod) {
        when (method) {
            PaymentMethod.Cash -> {
                binding.paymentIcon.setImageResource(R.drawable.money_draw)
                binding.paymentMethod.setText(R.string.cash)
            }
            PaymentMethod.Credit -> {
                binding.paymentIcon.setImageResource(R.drawable.credit_card_draw)
                binding.paymentMethod.setText(R.string.credit)
            }
            PaymentMethod.Debit -> {
                binding.paymentIcon.setImageResource(R.drawable.credit_card_draw)
                binding.paymentMethod.setText(R.string.debit)
            }
            PaymentMethod.Transfer -> {
                binding.paymentIcon.setImageResource(R.drawable.electronic_transfer_draw)
                binding.paymentMethod.setText(R.string.transfer)
            }
        }
        checkoutViewModel.updatePaymentMethod(method)
    }

    override fun onClick(confirm: Boolean) {
        if (!confirm) {
            binding.goOn.isEnabled = true
            return
        }
        binding.goOn.isEnabled = false
        checkoutViewModel.recordSale()
    }

}