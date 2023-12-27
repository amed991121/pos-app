package com.savent.erp.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.savent.erp.utils.PaymentMethod
import com.savent.erp.utils.PendingRemoteAction

@Entity(tableName = "debt_payments")
class DebtPaymentEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "remote_id")
    var remoteId: Int,
    @ColumnInfo(name = "sale_id")
    var saleId: Int,
    @ColumnInfo(name = "client_id")
    var clientId: Int,
    @ColumnInfo(name = "sale_timestamp")
    var saleTimestamp: Long,
    @ColumnInfo(name = "date_timestamp")
    var dateTimestamp: Long,
    @ColumnInfo(name = "to_pay")
    var toPay: Float,
    var paid: Float,
    @ColumnInfo(name = "payment_method")
    var paymentMethod: PaymentMethod,
    @ColumnInfo(name = "remote_action")
    var pendingRemoteAction: PendingRemoteAction
)