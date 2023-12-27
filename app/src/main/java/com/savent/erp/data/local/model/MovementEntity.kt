package com.savent.erp.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.utils.PendingRemoteAction

@Entity(tableName = "movements")
data class MovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "remote_id")
    val remoteId: Int,
    val type: MovementType,
    @ColumnInfo(name = "employee_id")
    val employeeId: Int,
    @ColumnInfo(name = "reason_id")
    val reasonId: Int,
    @ColumnInfo(name = "provider_id")
    val providerId: Int?,
    @ColumnInfo(name = "purchase_id")
    val purchaseId: Int?,
    @ColumnInfo(name = "selected_products")
    val selectedProducts: HashMap<Int, Int>,
    @ColumnInfo(name = "date_timestamp")
    val dateTimestamp: Long,
    @ColumnInfo(name = "store_input_id")
    val storeInputId: Int,
    @ColumnInfo(name = "store_output_id")
    val storeOutputId: Int,
    @ColumnInfo(name = "input_store_keeper_id")
    val inputStoreKeeperId: Int?,
    @ColumnInfo(name = "output_store_keeper_id")
    val outputStoreKeeperId: Int?,
    val units: Int,
    @ColumnInfo(name = "remote_action")
    val pendingRemoteAction: PendingRemoteAction
)