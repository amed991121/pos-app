package com.savent.erp.presentation.ui.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savent.erp.R
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.presentation.ui.adapters.ReasonsAdapter
import com.savent.erp.presentation.ui.model.FilterItem
import com.savent.erp.presentation.ui.model.MovementReasonItem

class ReasonsDialog(context: Context, reasons: List<MovementReasonItem>) : BottomSheetDialog(context),
    ReasonsAdapter.OnClickListener, FilterDialog.OnClickListener {

    private var _listener: OnEventListener? = null
    private var filterDialog: FilterDialog

    interface OnEventListener {
        fun onClick(reason: MovementReasonItem)
        fun onSearchReasons(query: String)
        fun onSetTypeFilter(type: MovementType)
        fun reasonsClosed()
    }


    fun setOnEventListener(listener: OnEventListener) {
        _listener = listener
    }

    private var reasonsAdapter: ReasonsAdapter

    fun setData(newReasons: List<MovementReasonItem>) {
        reasonsAdapter.setData(newReasons)
        filterDialog.setProgress(false)
    }

    init {
        setCancelable(true)
        setOnCancelListener { dismiss() }
        setContentView(R.layout.default_dialog_with_filter)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler)
        val title = findViewById<TextView>(R.id.title)
        val icon = findViewById<ImageView>(R.id.iconSearch)
        val close = findViewById<ImageView>(R.id.close)
        val searchView = findViewById<EditText>(R.id.searchView)
        val filter = findViewById<ImageView>(R.id.filter)

        title?.text = context.getString(R.string.movement_reasons)

        filterDialog =
            FilterDialog(
                context,
                getFilters(MovementType.ALL)
            )
        filterDialog.setOnClickListener(this)

        reasonsAdapter = ReasonsAdapter()
        reasonsAdapter.setOnClickListener(this)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        reasonsAdapter.setHasStableIds(true)
        recyclerView?.adapter = reasonsAdapter
        recyclerView?.itemAnimator = DefaultItemAnimator()
        setData(reasons)

        searchView?.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    _listener?.onSearchReasons(s.toString())
                    if (s.toString().isNotEmpty()) {
                        icon?.visibility = View.VISIBLE
                        icon?.setImageResource(R.drawable.ic_round_close_24)
                    } else {
                        icon?.setImageResource(R.drawable.ic_round_search_24)
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })

        icon?.setOnClickListener {
            searchView?.setText("")
        }

        filter?.setOnClickListener {
            filterDialog.show()
        }

        close?.setOnClickListener {
            dismiss()
        }

        setOnCancelListener {
            _listener?.reasonsClosed()
        }

        setOnDismissListener {
            _listener?.reasonsClosed()
        }

    }

    override fun onClick(reason: MovementReasonItem) {
        _listener?.onClick(reason)
        dismiss()
    }

    override fun selectFilter(filter: String) {
        val type = when (filter) {
            context.getString(R.string.input) -> MovementType.INPUT
            context.getString(R.string.output) -> MovementType.OUTPUT
            else -> MovementType.ALL
        }
        filterDialog.setData(getFilters(type))
        _listener?.onSetTypeFilter(type)
        filterDialog.dismiss()
    }

    private fun getFilters(filterType: MovementType) =
        listOf(
            FilterItem(context.getString(R.string.input), filterType == MovementType.INPUT),
            FilterItem(context.getString(R.string.output), filterType == MovementType.OUTPUT),
            FilterItem(context.getString(R.string.in_out), filterType == MovementType.ALL)
        )
}