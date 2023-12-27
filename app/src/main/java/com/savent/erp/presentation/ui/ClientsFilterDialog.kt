package com.savent.erp.presentation.ui

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savent.erp.R
import com.savent.erp.presentation.ui.adapters.FiltersAdapter
import com.savent.erp.presentation.ui.model.FilterItem

class ClientsFilterDialog(context: Context, filters: List<FilterItem>) :
    BottomSheetDialog(context),
    FiltersAdapter.OnClickListener {

    private lateinit var filtersAdapter: FiltersAdapter
    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun selectFilter(filter: String)
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    init {
        setCancelable(true)
        setOnCancelListener { dismiss() }
        setContentView(R.layout.clients_filter_dialog)

        val close = findViewById<ImageView>(R.id.close)
        setupRecyclerView()
        filtersAdapter.setData(filters)

        close?.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        filtersAdapter = FiltersAdapter()
        filtersAdapter.setOnClickListener(this)
        val recyclerView = findViewById<RecyclerView>(R.id.filters_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        filtersAdapter.setHasStableIds(true)
        recyclerView?.adapter = filtersAdapter
        recyclerView?.itemAnimator = DefaultItemAnimator()
    }

    fun setData(newFilters: List<FilterItem>) {
        filtersAdapter.setData(newFilters)
    }

    fun setProgress(progress: Boolean) {
        findViewById<ProgressBar>(R.id.progress)?.visibility =
            if (progress) View.VISIBLE else View.GONE
    }

    override fun onClick(filter: String) {
        _listener?.selectFilter(filter)
    }
}