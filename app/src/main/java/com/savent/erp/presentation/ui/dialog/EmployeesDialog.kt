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
import com.savent.erp.presentation.ui.adapters.EmployeesAdapter
import com.savent.erp.presentation.ui.model.EmployeeItem

class EmployeesDialog(context: Context, employees: List<EmployeeItem>) : BottomSheetDialog(context),
    EmployeesAdapter.OnClickListener {

    private var _listener: OnEventListener? = null

    interface OnEventListener {
        fun onClick(employee: EmployeeItem)
        fun onSearchEmployees(query: String)
    }


    fun setOnEventListener(listener: OnEventListener) {
        _listener = listener
    }

    private var employeesAdapter: EmployeesAdapter

    fun setData(newEmployees: List<EmployeeItem>) {
        employeesAdapter.setData(newEmployees)
    }

    init {
        setCancelable(true)
        setOnCancelListener { dismiss() }
        setContentView(R.layout.default_dialog)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler)
        val title = findViewById<TextView>(R.id.title)
        val icon = findViewById<ImageView>(R.id.iconSearch)
        val close = findViewById<ImageView>(R.id.close)
        val searchView = findViewById<EditText>(R.id.searchView)

        title?.text = context.getString(R.string.employees)

        employeesAdapter = EmployeesAdapter()
        employeesAdapter.setOnClickListener(this)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        employeesAdapter.setHasStableIds(true)
        recyclerView?.adapter = employeesAdapter
        recyclerView?.itemAnimator = DefaultItemAnimator()
        setData(employees)

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
                    _listener?.onSearchEmployees(s.toString())
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

        close?.setOnClickListener {
            dismiss()
        }

    }

    override fun onClick(employee: EmployeeItem) {
        _listener?.onClick(employee)
        dismiss()
    }
}