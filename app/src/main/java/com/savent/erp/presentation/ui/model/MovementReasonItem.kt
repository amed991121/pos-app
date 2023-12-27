package com.savent.erp.presentation.ui.model

import com.savent.erp.data.common.model.MovementType

data class MovementReasonItem(
    val id: Int,
    val name: String,
    val type: MovementType
)