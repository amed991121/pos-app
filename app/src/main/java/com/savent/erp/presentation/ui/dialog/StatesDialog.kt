package com.savent.erp.presentation.ui.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savent.erp.R


class StatesDialog(context: Context) : BottomSheetDialog(context) {

    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(store: String)
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    init {
        setCancelable(true)
        setOnCancelListener { dismiss() }
        setContentView(R.layout.states_dialog)

        val listView = findViewById<ListView>(R.id.list)
        val icon = findViewById<ImageView>(R.id.iconSearch)
        val close = findViewById<ImageView>(R.id.close)
        val searchView = findViewById<EditText>(R.id.searchView)


        val array = context.resources.getStringArray(R.array.mexico_states)
        val arrayAdapter = ArrayAdapter<String>(
            context,
            R.layout.string_layout,
            array
        )

        listView?.adapter = arrayAdapter

        listView?.setOnItemClickListener { parent, view, position, id ->
            _listener?.onClick(arrayAdapter.getItem(position)?:array[0])
            dismiss()
        }

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
                    arrayAdapter.filter.filter(s)
                    if (s.toString().isNotEmpty()) {
                        icon?.visibility = View.VISIBLE
                        icon?.setImageResource(R.drawable.ic_round_close_24)
                    }else {
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
}