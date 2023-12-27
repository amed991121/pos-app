package com.savent.erp.presentation.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.savent.erp.R
import com.savent.erp.databinding.FragmentClientsWithDebtsBinding
import com.savent.erp.presentation.ui.CustomSnackBar
import com.savent.erp.presentation.ui.adapters.ClientsWithDebtsAdapter
import com.savent.erp.presentation.ui.model.ClientDebt
import com.savent.erp.presentation.viewmodel.DebtsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClientsWithDebtsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClientsWithDebtsFragment : Fragment(), ClientsWithDebtsAdapter.OnClickListener {
    private lateinit var binding: FragmentClientsWithDebtsBinding
    private val debtsViewModel: DebtsViewModel by sharedViewModel()
    private lateinit var clientsWithDebtsAdapter: ClientsWithDebtsAdapter
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
            inflater, R.layout.fragment_clients_with_debts,
            container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = debtsViewModel

        initEvents()
        setupRecyclerView()

        return binding.root
    }

    private fun initEvents() {

        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let { debtsViewModel.loadClients(it.toString()) }
            }

        })

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
        clientsWithDebtsAdapter = ClientsWithDebtsAdapter()
        clientsWithDebtsAdapter.setOnClickListener(this)
        val recyclerView = binding.clientsRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        clientsWithDebtsAdapter.setHasStableIds(true)
        recyclerView.adapter = clientsWithDebtsAdapter
        recyclerView.itemAnimator = DefaultItemAnimator()

    }

    private fun subscribeToObservables() {
        debtsViewModel.clients.observe(this) {
            clientsWithDebtsAdapter.setData(it)
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
         * @return A new instance of fragment ClientsWithDebtsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClientsWithDebtsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(clientId: Int, clientDebt: ClientDebt) {
        debtsViewModel.loadIncompletePayments(clientId, clientDebt)
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_clientWithDebtsFragment_to_incompletePaymentsFragment)
    }
}