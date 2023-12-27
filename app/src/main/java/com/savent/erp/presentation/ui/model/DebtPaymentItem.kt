package com.savent.erp.presentation.ui.model

import com.savent.erp.utils.PaymentMethod

data class DebtPaymentItem(
    val id: Int,
    val clientName: String,
    val saleDateTime: String,
    val time: String,
    val paymentMethod: PaymentMethod,
    val toPay: Float,
    val collected: Float,
    val isPendingSending: Boolean
)