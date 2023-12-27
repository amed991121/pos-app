package com.savent.erp.presentation.ui

import android.app.AlarmManager
import android.view.View
import android.widget.TextView
import androidx.core.app.AlarmManagerCompat
import com.google.android.material.snackbar.Snackbar
import com.savent.erp.R

class CustomSnackBar {

    companion object {
        const val LENGTH_LONG = Snackbar.LENGTH_LONG
        fun make(view: View, text: CharSequence, duration: Int): Snackbar {
            val snackbar = Snackbar.make(view, text, duration)
            val tv =
                snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            tv.textSize = 21F
            return snackbar
        }
    }
}