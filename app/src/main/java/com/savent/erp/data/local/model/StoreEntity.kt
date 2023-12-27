package com.savent.erp.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stores")
data class StoreEntity(
    @ColumnInfo(name = "remote_id")
    val remoteId: Int,
    val name: String,
    @ColumnInfo(name ="company_id")
    val companyId: Int
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}