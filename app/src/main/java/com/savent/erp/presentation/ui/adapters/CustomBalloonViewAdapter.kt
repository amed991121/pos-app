package com.savent.erp.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.savent.erp.R
import com.tomtom.sdk.map.display.marker.BalloonViewAdapter
import com.tomtom.sdk.map.display.marker.Marker

class CustomBalloonViewAdapter(private val context: Context):BalloonViewAdapter {

    override fun onCreateBalloonView(marker: Marker): View {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_ballon_view, null)
        val balloonTv = view.findViewById<TextView>(R.id.name)
        balloonTv.text = marker.balloonText
        return balloonTv
    }
}