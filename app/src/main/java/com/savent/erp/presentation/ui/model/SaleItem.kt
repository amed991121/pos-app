package com.savent.erp.presentation.ui.model

import com.savent.erp.utils.PaymentMethod

data class SaleItem(
    val localId: Int,
    val remoteId: Int,
    val clientName: String,
    val time: String,
    val productsUnits: Int,
    val paymentMethod: PaymentMethod,
    val subtotal: Float,
    val taxes: Float,
    val discounts: Float,
    val toPay: Float,
    val collected: Float,
    val change: Float,
    val isPendingSending: Boolean
) {
}