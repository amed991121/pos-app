package com.savent.erp.data.remote.model

import com.google.gson.annotations.SerializedName
import com.savent.erp.utils.DateTimeObj

data class IncompletePayment(
    var id: Int,
    @SerializedName("sale_id")
    var saleId: Int,
    @SerializedName( "client_id")
    var clientId: Int,
    @SerializedName( "date_timestamp")
    var dateTimestamp:  DateTimeObj,
    @SerializedName( "products_units")
    var productsUnits: Int,
    var subtotal: Float,
    var discounts: Float,
    var IVA: Float,
    var collected: Float,
    var total: Float,
)