package com.savent.erp.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.savent.erp.utils.PendingRemoteAction

@Entity(tableName = "products")
class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "remote_id")
    var remoteId: Int,
    val name: String,
    val barcode: String?,
    val description: String,
    val image: String?,
    val price: Float,
    val IEPS: Float,
    val IVA: Float,
    var units: Int,
    @ColumnInfo(name = "data_timestamp")
    var dateTimestamp: Long,
    @ColumnInfo(name = "remote_action")
    var pendingRemoteAction: PendingRemoteAction
) {
}