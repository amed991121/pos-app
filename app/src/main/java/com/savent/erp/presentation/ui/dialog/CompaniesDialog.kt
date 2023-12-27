package com.savent.erp.presentation.ui.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savent.erp.R
import com.savent.erp.data.local.model.CompanyEntity
import com.savent.erp.presentation.ui.adapters.CompaniesAdapter


class CompaniesDialog(context: Context, companies: List<CompanyEntity>) :
    BottomSheetDialog(context), CompaniesAdapter.OnClickListener {

    private var _listener: OnEventListener? = null

    interface OnEventListener {
        fun onClick(company: CompanyEntity)
        fun onSearch(query: String)
    }


    fun setOnEventListener(listener: OnEventListener) {
        _listener = listener
    }

    private var companiesAdapter: CompaniesAdapter

    fun setData(newCompanies: List<CompanyEntity>) {
        companiesAdapter.setData(newCompanies)
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

        title?.text = context.getString(R.string.companies)

        companiesAdapter = CompaniesAdapter()
        companiesAdapter.setOnClickListener(this)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        companiesAdapter.setHasStableIds(true)
        recyclerView?.adapter = companiesAdapter
        recyclerView?.itemAnimator = DefaultItemAnimator()
        setData(companies)

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
                    _listener?.onSearch(s.toString())
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

    override fun onClick(company: CompanyEntity) {
        _listener?.onClick(company)
        dismiss()
    }
}