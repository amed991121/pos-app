package com.savent.erp.presentation.ui.dialog

import android.content.Context
import android.content.res.Resources.getSystem
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savent.erp.R
import com.savent.erp.presentation.ui.adapters.ProductsAdapter
import com.savent.erp.presentation.ui.model.ProductItem

class ProductsDialog(context: Context, products: List<ProductItem>) : BottomSheetDialog(context),
    ProductsAdapter.OnEventListener {

    private var _listener: OnEventListener? = null

    interface OnEventListener {
        fun onSearchProducts(query: String)
        fun addProduct(id: Int)
        fun removeProduct(id: Int)
        fun changeProductUnits(productId: Int, units: Int)
        fun goOn()
        fun productsClosed()
    }

    fun setOnEventListener(listener: OnEventListener) {
        _listener = listener
    }

    private var productsAdapter: ProductsAdapter

    fun setData(newProducts: List<ProductItem>) {
        productsAdapter.setData(newProducts)
    }

    init {
        setCancelable(true)
        setOnCancelListener { dismiss() }
        setContentView(R.layout.default_dialog_with_button)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler)
        val title = findViewById<TextView>(R.id.title)
        val icon = findViewById<ImageView>(R.id.iconSearch)
        val close = findViewById<ImageView>(R.id.close)
        val searchView = findViewById<EditText>(R.id.searchView)
        val goOn = findViewById<Button>(R.id.goOn)

        title?.text = context.getString(R.string.products)

        productsAdapter = ProductsAdapter(context)
        productsAdapter.setOnEventListener(this)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        productsAdapter.setHasStableIds(true)
        recyclerView?.adapter = productsAdapter
        recyclerView?.itemAnimator = DefaultItemAnimator()
        setData(products)

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
                    _listener?.onSearchProducts(s.toString())
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

        goOn?.setOnClickListener {
            _listener?.goOn()
        }

        setOnCancelListener {
            _listener?.productsClosed()
        }
        setOnDismissListener {
            _listener?.productsClosed()
        }

    }

    override fun onAddProductClick(id: Int) {
        _listener?.addProduct(id)
    }

    override fun onRemoveProductClick(id: Int) {
        _listener?.removeProduct(id)
    }

    override fun onChangeProductsUnitsClick(id: Int, units: Int) {
        _listener?.changeProductUnits(id, units)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            val view = currentFocus
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken,0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}