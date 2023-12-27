package com.savent.erp.data.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.savent.erp.utils.DateTimeObj

@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "movement_id")
    @SerializedName("movement_id")
    val movementId: Int,
    @ColumnInfo(name = "buyer_id")
    @SerializedName("buyer_id")
    val buyerId: Int,
    @ColumnInfo(name = "provider_id")
    @SerializedName("provider_id")
    val providerId: Int,
    val amount: Float,
    @ColumnInfo(name = "remaining_products")
    @SerializedName("remaining_products")
    val remainingProducts: HashMap<Int, Int>,
    @ColumnInfo(name = "date_timestamp")
    @SerializedName("date_timestamp")
    var dateTimestamp: DateTimeObj,
    @ColumnInfo(name = "total_units")
    @SerializedName("total_units")
    val totalUnits: Int,
    @ColumnInfo(name = "remaining_units")
    @SerializedName("remaining_units")
    val remainingUnits: Int
)