package com.savent.erp.presentation.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savent.erp.R
import com.savent.erp.utils.DecimalFormat
import com.savent.erp.utils.PaymentMethod

class DebtDialog(context: Context, toCollect: Float) : BottomSheetDialog(context) {

    private var _listenerPay: OnPayClickListener? = null
    private var method: PaymentMethod = PaymentMethod.Cash

    interface OnPayClickListener {
        fun payDebt(collect: Float, paymentMethod: PaymentMethod)
    }

    fun setOnPayClickListener(listenerPay: OnPayClickListener) {
        _listenerPay = listenerPay
    }

    init {
        setCancelable(true)
        setOnCancelListener { dismiss() }
        setContentView(R.layout.debt_dialog)

        val close = findViewById<ImageView>(R.id.close)
        val paymentIcon = findViewById<ImageView>(R.id.payment_icon)
        val paymentMethod = findViewById<Spinner>(R.id.payment_method)
        val toCollectTv = findViewById<TextView>(R.id.to_collect)
        val changeTv = findViewById<TextView>(R.id.change)
        val collectTv = findViewById<EditText>(R.id.collect)
        val goBtn = findViewById<Button>(R.id.go_on)

        val arrayAdapter = ArrayAdapter<String>(
            context,
            R.layout.payment_method_item,
            context.resources.getStringArray(R.array.payment_method)
        )
        paymentMethod?.adapter = arrayAdapter
        "$${DecimalFormat.format(toCollect)}".also { toCollectTv?.text = it }

        paymentMethod?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        method = PaymentMethod.Cash
                        paymentIcon?.setImageResource(R.drawable.ic_round_payments_24)
                    }
                    1 -> {
                        method = PaymentMethod.Credit
                        paymentIcon?.setImageResource(R.drawable.ic_round_credit_card_24)
                    }
                    2 -> {
                        method = PaymentMethod.Debit
                        paymentIcon?.setImageResource(R.drawable.ic_round_money_off_24)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        collectTv?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty() || (s.length == 1 && s[0] == '.')) {
                    collectTv.setText("0")
                    collectTv.setSelection(collectTv.text.length)
                    return
                }
                val strCollected = s.toString()
                if (strCollected[0] == '0' && strCollected.length > 1
                    && strCollected[1].isDigit()
                ) {
                    collectTv.setText(strCollected.substring(1))
                    collectTv.setSelection(collectTv.text.length)
                    return
                }
                collectTv.text.toString().toFloat().let {
                    if(it > toCollect) changeTv?.text = "$${DecimalFormat.format(it - toCollect)}"
                    else changeTv?.text =  "$${DecimalFormat.format(0F)}"

                }

            }

        })

        close?.setOnClickListener {
            dismiss()
        }
        goBtn?.setOnClickListener {
            if (collectTv?.text?.isEmpty() == true || collectTv?.text?.toString()?.toFloat()
                    ?.equals(0F) == true
            ) {
                Toast.makeText(
                    context,
                    context.resources.getText(R.string.register_amount_collected),
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            _listenerPay?.payDebt(collectTv?.text.toString().toFloat(), method)
        }
    }
}