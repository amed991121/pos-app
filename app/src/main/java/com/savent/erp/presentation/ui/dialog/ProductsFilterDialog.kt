package com.savent.erp.presentation.ui.dialog

import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savent.erp.R
import com.savent.erp.presentation.ui.adapters.FiltersAdapter
import com.savent.erp.presentation.ui.model.FilterItem

class ProductsFilterDialog(context: Context, filters: List<FilterItem>, loadDiscounts: Boolean):
   BottomSheetDialog(context),
    FiltersAdapter.OnClickListener  {

    private lateinit var filtersAdapter: FiltersAdapter
    private var _listener: OnClickListener? = null
    private lateinit var filter: String
    private var check: CheckBox? = null

    interface OnClickListener {
        fun selectFilter(filter: String, loadDiscounts: Boolean)
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    init {
        setCancelable(true)
        setOnCancelListener { dismiss() }
        setContentView(R.layout.products_filter_dialog)

        val close = findViewById<ImageView>(R.id.close)
        val discountLayout = findViewById<ConstraintLayout>(R.id.discount_layout)
        check = findViewById<CheckBox>(R.id.check)
        setupRecyclerView()
        filtersAdapter.setData(filters)

        filters.forEach {
            if (it.isSelected) {
                filter = it.filter
                return@forEach
            }
        }
        check?.isChecked = loadDiscounts

        discountLayout?.setOnClickListener {
            check?.isChecked = !(check?.isChecked?:loadDiscounts)
            _listener?.selectFilter(filter, check?.isChecked?:false)
        }
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
        this.filter = filter
        _listener?.selectFilter(filter, check?.isChecked?:false)
    }

}