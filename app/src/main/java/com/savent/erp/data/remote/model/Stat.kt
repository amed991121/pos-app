package com.savent.erp.data.remote.model

class Stat(
    val localId: Int,
    val metricsName: String,
    val periodInDays: Int,
    val value: Float,
    val percent: Float,
    val trend: Trend
) {

}