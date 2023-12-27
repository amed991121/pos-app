package com.savent.erp.presentation.ui.dialog

import android.content.Context
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savent.erp.R
import com.savent.erp.utils.PaymentMethod

class SyncDialog(context: Context) : BottomSheetDialog(context) {

    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick()
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    init {
        setCancelable(true)
        setOnCancelListener { dismiss() }
        setContentView(R.layout.sync_dialog)

        val sync = findViewById<ConstraintLayout>(R.id.sync)
        val closeDialog = findViewById<ImageView>(R.id.close)

        sync?.setOnClickListener {
            _listener?.onClick()
            dismiss()
        }

        closeDialog?.setOnClickListener {
            dismiss()
        }
    }
}