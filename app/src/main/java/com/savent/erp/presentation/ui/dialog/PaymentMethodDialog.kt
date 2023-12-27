package com.savent.erp.presentation.ui.dialog

import android.content.Context
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savent.erp.R
import com.savent.erp.utils.PaymentMethod

class PaymentMethodDialog(context: Context) : BottomSheetDialog(context) {

    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(method: PaymentMethod)
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    init {
        setCancelable(true)
        setOnCancelListener { dismiss() }
        setContentView(R.layout.payment_method_dialog)

        val cash = findViewById<ConstraintLayout>(R.id.cash)
        val credit = findViewById<ConstraintLayout>(R.id.credit)
        val debit = findViewById<ConstraintLayout>(R.id.debit)
        val transfer = findViewById<ConstraintLayout>(R.id.transfer)
        val closeDialog = findViewById<ImageView>(R.id.close)

        cash?.setOnClickListener {
            _listener?.onClick(PaymentMethod.Cash)
            dismiss()
        }
        credit?.setOnClickListener {
            _listener?.onClick(PaymentMethod.Credit)
            dismiss()
        }

        debit?.setOnClickListener {
            _listener?.onClick(PaymentMethod.Debit)
            dismiss()
        }

        transfer?.setOnClickListener {
            _listener?.onClick(PaymentMethod.Transfer)
            dismiss()
        }

        closeDialog?.setOnClickListener {
            dismiss()
        }
    }
}