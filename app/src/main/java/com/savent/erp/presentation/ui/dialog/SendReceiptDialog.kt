package com.savent.erp.presentation.ui.dialog

import android.content.Context
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savent.erp.R

class SendReceiptDialog(context: Context) : BottomSheetDialog(context) {

    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun sendByWhatsapp()
        fun sendBySms()
        fun sendByEmail()
        fun sendPrintable()
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    init {
        setCancelable(true)
        setOnCancelListener { dismiss() }
        setContentView(R.layout.shared_receipt_dialog)

        val whatsapp = findViewById<ConstraintLayout>(R.id.whatsapp)
        val sms = findViewById<ConstraintLayout>(R.id.sms)
        val email = findViewById<ConstraintLayout>(R.id.email)
        val bluetoothPrinter = findViewById<ConstraintLayout>(R.id.bluetooth_printer)
        val closeDialog = findViewById<ImageView>(R.id.close)

        whatsapp?.setOnClickListener {
            _listener?.sendByWhatsapp()
        }
        sms?.setOnClickListener {
            _listener?.sendBySms()
        }

        email?.setOnClickListener {
            _listener?.sendByEmail()
        }

        bluetoothPrinter?.setOnClickListener {
            _listener?.sendPrintable()
        }

        closeDialog?.setOnClickListener {
            dismiss()
        }
    }
}