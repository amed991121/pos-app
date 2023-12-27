package com.savent.erp.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.savent.erp.utils.PaymentMethod
import com.savent.erp.utils.PendingRemoteAction

@Entity(tableName = "sales")
class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "remote_id")
    var remoteId: Int = 0,
    @ColumnInfo(name = "client_id")
    var clientId: Int = 0,
    @ColumnInfo(name = "client_name")
    var clientName: String = "",
    @ColumnInfo(name = "date_timestamp")
    var dateTimestamp: Long = 0L,
    @ColumnInfo(name = "selected_products")
    var selectedProducts: HashMap<Int, Int> = HashMap(),
    var subtotal: Float = 0F,
    var discounts: Float = 0F,
    var IVA: Float = 0F,
    var IEPS: Float = 0F,
    @ColumnInfo(name = "extra_discount_percent")
    var extraDiscountPercent: Int = 0,
    var collected: Float = 0F,
    var total: Float = 0F,
    @ColumnInfo(name = "payment_method")
    var paymentMethod: PaymentMethod = PaymentMethod.Credit,
    @ColumnInfo(name = "remote_action")
    var pendingRemoteAction: PendingRemoteAction = PendingRemoteAction.INSERT
) {
}