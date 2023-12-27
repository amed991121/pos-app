package com.savent.erp.data.remote.model


import com.google.gson.annotations.SerializedName
import com.savent.erp.utils.DateTimeObj
import com.savent.erp.utils.PaymentMethod

data class Sale(
    var id: Int = 0,
    @SerializedName("client_id")
    var clientId: Int? = null,
    @SerializedName("client_name")
    var clientName: String? = null,
    @SerializedName("date_timestamp")
    var dateTimestamp: DateTimeObj? = null,
    @SerializedName("products_selected")
    var selectedProducts: HashMap<Int, Int> = HashMap(),
    var subtotal: Float = 0F,
    var discounts: Float = 0F,
    var IVA: Float = 0F,
    var IEPS: Float = 0F,
    @SerializedName("extra_discount_percent")
    var extraDiscountPercent: Int = 0,
    var collected: Float = 0F,
    var total: Float = 0F,
    @SerializedName("payment_method")
    var paymentMethod: PaymentMethod = PaymentMethod.Cash,
) {
}