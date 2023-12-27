package com.savent.erp.data.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "discounts",
    indices = [Index(value = ["product_id", "client_id"], unique = true)]
)
data class Discount(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @SerializedName("product_id")
    @ColumnInfo(name = "product_id")
    val productId: Int,
    @SerializedName("client_id")
    @ColumnInfo(name = "client_id")
    val clientId: Int,
    val value: Float,
)