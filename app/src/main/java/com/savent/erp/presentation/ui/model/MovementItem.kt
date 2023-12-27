package com.savent.erp.presentation.ui.model
import com.savent.erp.data.common.model.MovementType

data class MovementItem (
    val localId: Int,
    val remoteId: Int,
    val type: MovementType,
    val reason: String,
    val date: String,
    val time: String,
    val employee: String,
    val purchaseId: Int?,
    val inputStore: String?,
    val outputStore: String?,
    val inputStoreKeeper: String?,
    val outputStoreKeeper: String?,
    val productsUnits: Int,
    val isPendingSending: Boolean
)