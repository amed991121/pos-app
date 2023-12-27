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
import com.savent.erp.presentation.ui.adapters.PurchasesAdapter
import com.savent.erp.presentation.ui.model.PurchaseItem

class PurchasesDialog(context: Context, purchases: List<PurchaseItem>) : BottomSheetDialog(context),
    PurchasesAdapter.OnClickListener {

    private var _listener: OnEventListener? = null

    interface OnEventListener {
        fun onClick(purchase: PurchaseItem)
        fun onSearchPurchases(query: String)
    }

    fun setOnEventListener(listener: OnEventListener) {
        _listener = listener
    }

    private var purchasesAdapter: PurchasesAdapter

    fun setData(newPurchases: List<PurchaseItem>) {
        purchasesAdapter.setData(newPurchases)
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

        title?.text = context.getString(R.string.purchases)

        purchasesAdapter = PurchasesAdapter()
        purchasesAdapter.setOnClickListener(this)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        purchasesAdapter.setHasStableIds(true)
        recyclerView?.adapter = purchasesAdapter
        recyclerView?.itemAnimator = DefaultItemAnimator()
        setData(purchases)

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
                    _listener?.onSearchPurchases(s.toString())
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

    override fun onClick(purchase: PurchaseItem) {
        _listener?.onClick(purchase)
        dismiss()
    }
}