package com.savent.erp.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.savent.erp.AppConstants
import com.savent.erp.R
import com.savent.erp.databinding.FragmentCreateSaleBinding
import com.savent.erp.domain.usecase.LocationRequestUseCase
import com.savent.erp.presentation.ui.CustomSnackBar
import com.savent.erp.presentation.ui.activity.MapActivity
import com.savent.erp.presentation.ui.adapters.ClientsAdapter
import com.savent.erp.presentation.ui.dialog.FilterDialog
import com.savent.erp.presentation.ui.model.FilterItem
import com.savent.erp.presentation.viewmodel.ClientsViewModel
import com.savent.erp.presentation.viewmodel.MainViewModel
import com.savent.erp.utils.IsLocationGranted
import com.savent.erp.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateSaleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateSaleFragment : Fragment(), ClientsAdapter.OnClickListener,
    FilterDialog.OnClickListener {
    private lateinit var binding: FragmentCreateSaleBinding
    private val mainViewModel: MainViewModel by sharedViewModel()
    private val clientsViewModel: ClientsViewModel by sharedViewModel()
    private lateinit var clientsAdapter: ClientsAdapter
    private var filterDialog: FilterDialog? = null
    private var defaultJob: Job? = null
    private var clientsSize: Int = 0
    private var clientId: Int = -1
    private var currentLatLng: LatLng? = null
    private var isWaitingForLocation = false
    private val locationRequestUseCase = LocationRequestUseCase()
    private var requestLocationJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_create_sale,
            container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = clientsViewModel

        initEvents()
        setupRecyclerView()
        subscribeToObservables()
        return binding.root
    }

    private fun initEvents() {
        binding.goOn.setOnClickListener {
            clientsViewModel.await()
        }

        lifecycleScope.launchWhenCreated {
            if (!clientsViewModel.isCreateClientAvailable())
                binding.addClient.visibility = View.GONE
        }

        lifecycleScope.launchWhenCreated {
            clientsViewModel.loadClients()
        }

        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let { clientsViewModel.loadClients(it.toString()) }
            }

        })

        binding.refreshLayout.setOnRefreshListener {
            defaultJob?.cancel()
            defaultJob = lifecycleScope.launch(Dispatchers.IO) {
                if (clientsViewModel.isInternetAvailable())
                    clientsViewModel.syncClientsIOData()
            }

        }

        binding.addClient.setOnClickListener {
            mainViewModel.goToDestination(R.id.addClientFragment)
        }

        binding.goMap.setOnClickListener {
            startActivity(Intent(activity, MapActivity::class.java))
        }

        binding.filter.setOnClickListener {
            filterDialog = FilterDialog(
                requireContext(),
                getClientsFilterItems(clientsViewModel.appPreferences.value?.clientsFilter ?: "")
            )
            filterDialog?.setOnClickListener(this)
            filterDialog?.show()
        }

        binding.backButton.setOnClickListener {
            mainViewModel.back()
        }
    }

    private fun setupRecyclerView() {
        clientsAdapter = ClientsAdapter()
        clientsAdapter.setOnClickListener(this)
        val recyclerView = binding.clientsRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        clientsAdapter.setHasStableIds(true)
        recyclerView.adapter = clientsAdapter
        recyclerView.itemAnimator = DefaultItemAnimator()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager: LinearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
                val firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (lastVisibleItem == clientsSize - 1 && (lastVisibleItem - firstVisibleItem) != clientsSize - 1)
                    binding.goMapIcon.visibility = View.GONE
                else
                    binding.goMapIcon.visibility = View.VISIBLE

            }
        })

    }

    private fun subscribeToObservables() {
        clientsViewModel.clients.observe(viewLifecycleOwner) {
            clientsSize = it.size
            clientsAdapter.setData(it)
        }

        clientsViewModel.loading.observe(viewLifecycleOwner) {
            binding.refreshLayout.isRefreshing = it
            filterDialog?.setProgress(it)
        }

        clientsViewModel.appPreferences.observe(viewLifecycleOwner) {
            filterDialog?.setData(getClientsFilterItems(it?.clientsFilter ?: ""))
        }

        mainViewModel.latLng.observe(viewLifecycleOwner) {
            currentLatLng = it
            mainViewModel.removeLocationUpdates()
            if (clientId != -1)
                clientsViewModel.updateClientLocation(currentLatLng ?: LatLng(0.0, 0.0), clientId)
            if (isWaitingForLocation) mainViewModel.goOn()
        }

        mainViewModel.uiEvent.observe(viewLifecycleOwner) { uiEvent ->
            when (uiEvent) {
                is MainViewModel.UiEvent.ShowMessage -> {
                    Toast.makeText(
                        context,
                        uiEvent.resId?.let { getString(uiEvent.resId) }
                            ?: getString(R.string.unknown_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {}
            }
        }

        lifecycleScope.launchWhenCreated {
            clientsViewModel.uiEvent.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { uiEvent ->
                    when (uiEvent) {
                        is ClientsViewModel.UiEvent.ShowMessage -> {
                            try {
                                CustomSnackBar.make(
                                    binding.root,
                                    uiEvent.resId?.let { getString(it) } ?: uiEvent.message
                                    ?: getString(R.string.unknown_error),
                                    CustomSnackBar.LENGTH_LONG
                                ).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        is ClientsViewModel.UiEvent.Continue -> {
                            if (uiEvent.available) {
                                if (!IsLocationGranted(requireContext())) {
                                    requestLocationPermission()
                                    return@collectLatest
                                }
                                if (currentLatLng == null) {
                                    runLocationRequest()
                                    isWaitingForLocation = true
                                    CustomSnackBar.make(
                                        binding.root,
                                        getString(R.string.locating_you),
                                        CustomSnackBar.LENGTH_LONG
                                    ).show()
                                    return@collectLatest
                                }
                                mainViewModel.goOn()
                            }
                        }
                        else -> {

                        }
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
         * @return A new instance of fragment CreateSaleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateSaleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(id: Int) {
        clientId = id
        clientsViewModel.addClientToSale(id)
    }

    override fun selectFilter(filter: String) {
        clientsViewModel.changeClientsFilter(filter)
    }

    private fun getClientsFilterItems(clientsFiltersPreferences: String): List<FilterItem> {
        val clientsFilters = resources.getStringArray(R.array.clients_filter)
        val clientsFilterItems = mutableListOf<FilterItem>()

        clientsFilters.forEach {
            clientsFilterItems.add(FilterItem(it, clientsFiltersPreferences == it))
        }
        return clientsFilterItems
    }

    private fun runLocationRequest() {
        requestLocationJob?.cancel()
        requestLocationJob = lifecycleScope.launch {
            activity?.let { it1 ->
                locationRequestUseCase(it1).collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            Log.d("log_","success run Location")
                            mainViewModel.runLocationUpdates()
                        }
                        else -> {
                            Log.d("log_","fail run Location")
                            requestLocationJob?.cancel()
                        }
                    }
                }
            }
        }
    }

    private fun requestLocationPermission() {
        activity?.requestPermissions(
            AppConstants.LOCATION_PERMISSIONS, AppConstants.REQUEST_LOCATION_PERMISSION_CODE
        )
    }

}