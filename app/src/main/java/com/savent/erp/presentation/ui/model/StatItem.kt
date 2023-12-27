package com.savent.erp.presentation.ui.model

import com.savent.erp.data.remote.model.Trend

data class StatItem(
    val localId: Int,
    val metricsName: String,
    val image: String?,
    val value: Float,
    val percent: Float,
    val trend: Trend
)
