package com.savent.erp.presentation.ui.model

import com.savent.erp.utils.PaymentMethod

class Checkout(
    val clientName: String,
    val image:String?,
    val productsUnits: Int,
    val subtotal: Float,
    val taxes: Float,
    val totalDiscounts: Float,
    val finalPrice: Float,
    val extraDiscountPercent: Int,
    val paymentMethod: PaymentMethod,
    val collected: Float,
    val change: Float
) {
}