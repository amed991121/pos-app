package com.savent.erp.data.remote.model

import com.google.gson.annotations.SerializedName
import com.savent.erp.utils.DateTimeObj
import com.savent.erp.utils.PaymentMethod

data class DebtPayment(
    var id: Int,
    @SerializedName("sale_id")
    var saleId: Int,
    @SerializedName("client_id")
    var clientId: Int,
    @SerializedName( "sale_timestamp")
    var saleTimestamp: DateTimeObj,
    @SerializedName( "date_timestamp")
    var dateTimestamp: DateTimeObj,
    @SerializedName( "to_pay")
    var toPay: Float,
    var paid: Float,
    @SerializedName( "payment_method")
    var paymentMethod: PaymentMethod,
)
