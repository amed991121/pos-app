package com.savent.erp.presentation.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.savent.erp.R
import com.savent.erp.data.remote.model.DebtPayment
import com.savent.erp.databinding.FragmentIncompletePaymentsBinding
import com.savent.erp.presentation.ui.CustomSnackBar
import com.savent.erp.presentation.ui.dialog.DebtDialog
import com.savent.erp.presentation.ui.adapters.IncompletePaymentsAdapter
import com.savent.erp.presentation.viewmodel.DebtsViewModel
import com.savent.erp.utils.DateFormat
import com.savent.erp.utils.DateTimeObj
import com.savent.erp.utils.PaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IncompletePaymentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IncompletePaymentsFragment : Fragment(), IncompletePaymentsAdapter.OnPayClickListener,
    DebtDialog.OnPayClickListener {
    private lateinit var binding: FragmentIncompletePaymentsBinding
    private val debtsViewModel: DebtsViewModel by sharedViewModel()
    private lateinit var incompletePaymentsAdapter: IncompletePaymentsAdapter
    private var debtDialog: DebtDialog? = null
    private var saleId: Int = 0
    private var saleDateTime = ""
    private var debt: Float = 0F
    private var defaultJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeToObservables()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_incomplete_payments,
            container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = debtsViewModel

        initEvents()
        setupRecyclerView()

        return binding.root
    }

    private fun initEvents() {

        binding.refreshLayout.setOnRefreshListener {
            defaultJob?.cancel()
            defaultJob = lifecycleScope.launch(Dispatchers.IO) {
                if (debtsViewModel.isInternetAvailable())
                    debtsViewModel.syncDebtIOData()
            }

        }

        binding.backButton.setOnClickListener {
            debtsViewModel.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        incompletePaymentsAdapter = IncompletePaymentsAdapter(context)
        incompletePaymentsAdapter.setOnPayClickListener(this)
        val recyclerView = binding.incompletePaymentsRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        incompletePaymentsAdapter.setHasStableIds(true)
        recyclerView.adapter = incompletePaymentsAdapter
        recyclerView.itemAnimator = DefaultItemAnimator()

    }

    private fun subscribeToObservables() {
        debtsViewModel.incompletePayments.observe(this) {
            incompletePaymentsAdapter.setData(it)
        }

        debtsViewModel.loading.observe(this){
            binding.refreshLayout.isRefreshing = it
        }

        lifecycleScope.launchWhenCreated {
            debtsViewModel.uiEvent.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { uiEvent ->
                    when (uiEvent) {
                        is DebtsViewModel.UiEvent.ShowMessage -> {
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
         * @return A new instance of fragment IncompletePaymentsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IncompletePaymentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onPayClick(saleId: Int, debt: Float, dateTime: String) {
        this.saleId = saleId
        this.debt = debt
        saleDateTime = dateTime

        debtDialog = context?.let { DebtDialog(it, debt) }
        debtDialog?.setOnPayClickListener(this)
        debtDialog?.show()
    }

    override fun payDebt(collect: Float, paymentMethod: PaymentMethod) {
        val timestamp = System.currentTimeMillis();
        debtsViewModel.payDebt(
            DebtPayment(
                Integer.MAX_VALUE,
                saleId,
                debtsViewModel.clientId.value ?: 0,
                DateTimeObj(
                    saleDateTime,
                    DateFormat.format(System.currentTimeMillis(), "HH:mm:ss.SSS")
                ),
                DateTimeObj(
                    DateFormat.format(timestamp, "yyyy-MM-dd"),
                    DateFormat.format(timestamp, "HH:mm:ss.SSS")
                ),
                debt - collect,
                collect,
                paymentMethod
            ),
            saleId
        )
        debtDialog?.dismiss()
    }
}