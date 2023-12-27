package com.savent.erp.presentation.ui

import android.content.Context
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savent.erp.R

class ExitDialog (context: Context) : BottomSheetDialog(context) {

    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun exit()
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    init {
        setCancelable(true)
        setOnCancelListener { dismiss() }
        setContentView(R.layout.exit_dialog)

        val exit = findViewById<ConstraintLayout>(R.id.exit)
        val cancel = findViewById<ConstraintLayout>(R.id.cancel)
        val close = findViewById<ImageView>(R.id.close)

        exit?.setOnClickListener {
            _listener?.exit()
        }
        cancel?.setOnClickListener {
            dismiss()
        }

        close?.setOnClickListener {
            dismiss()
        }
    }
}