package com.savent.erp.presentation.ui.model

data class IncompletePaymentItem(
    val localId: Int,
    val saleId: Int,
    val dateTime: String,
    val productsUnits: Int,
    val subtotal: Float,
    val taxes: Float,
    val discounts: Float,
    val toPay: Float,
    val collected: Float,
    val debts: Float
)