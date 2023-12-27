package com.savent.erp.presentation.ui.dialog

import android.content.Context
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savent.erp.R

class ConfirmSaleDialog(context: Context): BottomSheetDialog(context){
    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(confirm: Boolean)
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    init {
        setCancelable(true)
        setContentView(R.layout.confirm_sale_dialog)

        val confirm = findViewById<ConstraintLayout>(R.id.confirm_action)
        val cancel = findViewById<ConstraintLayout>(R.id.cancel_action)
        val closeDialog = findViewById<ImageView>(R.id.close)

        confirm?.setOnClickListener {
            _listener?.onClick(true)
            dismiss()
        }
        cancel?.setOnClickListener {
            _listener?.onClick(false)
            dismiss()
        }

        closeDialog?.setOnClickListener {
            _listener?.onClick(false)
            dismiss()
        }
        setOnCancelListener {
            _listener?.onClick(false)
            dismiss()
        }
    }

}