package com.savent.erp.presentation.ui.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savent.erp.R
import com.savent.erp.utils.DecimalFormat
import com.savent.erp.utils.PaymentMethod

class DebtDialog(context: Context, toCollect: Float) : BottomSheetDialog(context),
    PaymentMethodDialog.OnClickListener {

    private var _listenerPay: OnPayClickListener? = null
    private var method: PaymentMethod = PaymentMethod.Credit
    private var  paymentIcon: ImageView? = null
    private var paymentMethod: TextView? = null

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
        val paymentContainer = findViewById<ConstraintLayout>(R.id.payment_container)
        paymentIcon = findViewById<ImageView>(R.id.payment_icon)
        paymentMethod = findViewById<TextView>(R.id.payment_method)
        val toCollectTv = findViewById<TextView>(R.id.to_collect)
        val changeTv = findViewById<TextView>(R.id.change)
        val collectTv = findViewById<EditText>(R.id.collect)
        val goBtn = findViewById<TextView>(R.id.go_on)

        "$${DecimalFormat.format(toCollect)}".also { toCollectTv?.text = it }

        paymentContainer?.setOnClickListener {
            val paymentMethodDialog = PaymentMethodDialog(context)
            paymentMethodDialog.setOnClickListener(this)
            paymentMethodDialog.show()
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
                    if (it > toCollect) changeTv?.text = "$${DecimalFormat.format(it - toCollect)}"
                    else changeTv?.text = "$${DecimalFormat.format(0F)}"

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
            goBtn.isEnabled = false
            val collected = collectTv?.text.toString().toFloat().let {
                if (it > toCollect)
                    toCollect
                else it
            }
            _listenerPay?.payDebt(collected, method)
        }
    }

    override fun onClick(method: PaymentMethod) {
        this.method = method
        when (method) {
            PaymentMethod.Cash -> {
                paymentIcon?.setImageResource(R.drawable.money_draw)
                paymentMethod?.setText(R.string.cash)
            }
            PaymentMethod.Credit -> {
                paymentIcon?.setImageResource(R.drawable.credit_card_draw)
                paymentMethod?.setText(R.string.credit)
            }
            PaymentMethod.Debit -> {
                paymentIcon?.setImageResource(R.drawable.credit_card_draw)
                paymentMethod?.setText(R.string.debit)
            }
            PaymentMethod.Transfer -> {
                paymentIcon?.setImageResource(R.drawable.electronic_transfer_draw)
                paymentMethod?.setText(R.string.transfer)
            }
        }
    }
}