package com.savent.erp.data.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movement_reasons")
data class MovementReason (
    @PrimaryKey
    val id: Int,
    val name: String,
    val type: MovementType
)