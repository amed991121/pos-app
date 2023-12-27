package com.savent.erp.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.savent.erp.utils.PaymentMethod
import com.savent.erp.utils.PendingRemoteAction

@Entity(tableName = "incomplete_payments")
data class IncompletePaymentEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "sale_id")
    var saleId: Int,
    @ColumnInfo(name = "client_id")
    var clientId: Int,
    @ColumnInfo(name = "date_timestamp")
    var dateTimestamp: Long,
    @ColumnInfo(name = "products_units")
    var productsUnits: Int,
    var subtotal: Float,
    var discounts: Float,
    var IVA: Float,
    var collected: Float,
    var total: Float,
    @ColumnInfo(name = "remote_action")
    var pendingRemoteAction: PendingRemoteAction
)