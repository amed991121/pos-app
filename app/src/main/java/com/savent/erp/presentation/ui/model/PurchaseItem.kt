package com.savent.erp.presentation.ui.model

data class PurchaseItem(
    val id: Int,
    val buyer: String,
    val provider: String,
    val amount: String,
    val remainingUnits: Int,
    val date: String,
    val time: String
)