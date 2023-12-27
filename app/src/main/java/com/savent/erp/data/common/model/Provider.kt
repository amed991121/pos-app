package com.savent.erp.data.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "providers")
data class Provider (
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "trade_name")
    @SerializedName("trade_name")
    val tradeName: String,
    val name: String,
    @ColumnInfo(name = "paternal_name")
    @SerializedName("paternal_name")
    val paternalName: String,
    @ColumnInfo(name = "maternal_name")
    @SerializedName("maternal_name")
    val maternalName: String,
)