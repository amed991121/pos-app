package com.savent.erp.presentation.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.savent.erp.R
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.databinding.FragmentMovementsListBinding
import com.savent.erp.presentation.ui.CustomSnackBar
import com.savent.erp.presentation.ui.adapters.MovementsAdapter
import com.savent.erp.presentation.ui.dialog.FilterDialog
import com.savent.erp.presentation.ui.model.FilterItem
import com.savent.erp.presentation.viewmodel.MovementsViewModel
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
 * Use the [MovementsListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MovementsListFragment : Fragment(), FilterDialog.OnClickListener {
    private lateinit var binding: FragmentMovementsListBinding
    private val movementsViewModel: MovementsViewModel by sharedViewModel()
    private lateinit var movementsAdapter: MovementsAdapter
    private var filterType = MovementType.ALL
    private var query = ""
    private var filterDialog: FilterDialog? = null
    private var movementsSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeToObservables()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_movements_list,
            container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = movementsViewModel
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
                s?.let {
                    query = it.toString()
                    movementsViewModel.loadMovements(filterType, query)
                }
            }

        })

        binding.refreshLayout.setOnRefreshListener {
            movementsViewModel.executeNetworkOps()
        }

        binding.filter.setOnClickListener {
            filterDialog = FilterDialog(
                requireContext(), getFilters()
            )
            filterDialog?.setOnClickListener(this)
            filterDialog?.show()
        }

        binding.addMovement.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_movementsListFragment_to_createMovementFragment)
        }

        binding.backButton.setOnClickListener {
            movementsViewModel.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        movementsAdapter = MovementsAdapter(requireContext())
        val recyclerView = binding.movementsRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        movementsAdapter.setHasStableIds(true)
        recyclerView.adapter = movementsAdapter
        recyclerView.itemAnimator = DefaultItemAnimator()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager: LinearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
                val firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()

                if (lastVisibleItem == movementsSize - 1 && (lastVisibleItem - firstVisibleItem) != movementsSize - 1)
                    binding.addMovement.visibility = View.GONE
                else
                    binding.addMovement.visibility = View.VISIBLE

            }
        })
    }

    private fun subscribeToObservables() {
        movementsViewModel.movements.observe(this) {
            movementsAdapter.setData(it)
            movementsSize = it.size
        }

        movementsViewModel.loading.observe(this) {
            binding.refreshLayout.isRefreshing = it
        }

        lifecycleScope.launchWhenCreated {
            movementsViewModel.uiEvent.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { uiEvent ->
                    when (uiEvent) {
                        is MovementsViewModel.UiEvent.ShowMessage -> {
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

    override fun onStart() {
        super.onStart()
        movementsViewModel.executeNetworkOps()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MovementsListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MovementsListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun selectFilter(filter: String) {
        filterType = when (filter) {
            getString(R.string.input) -> MovementType.INPUT
            getString(R.string.output) -> MovementType.OUTPUT
            else -> MovementType.ALL
        }
        filterDialog?.setData(getFilters())
        movementsViewModel.loadMovements(filterType, query)
        filterDialog?.dismiss()
    }

    private fun getFilters() =
        listOf(
            FilterItem(getString(R.string.input), filterType == MovementType.INPUT),
            FilterItem(getString(R.string.output), filterType == MovementType.OUTPUT),
            FilterItem(getString(R.string.all), filterType == MovementType.ALL)
        )

}