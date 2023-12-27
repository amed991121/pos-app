package com.savent.erp.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.savent.erp.data.remote.model.Trend
import com.savent.erp.utils.PendingRemoteAction

@Entity(tableName = "stats")
class StatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "remote_id")
    val remoteId: Int,
    @ColumnInfo(name = "metrics_name")
    val metricsName: String,
    val image: String?,
    @ColumnInfo(name = "period_in_days")
    val periodInDays: Int,
    val value: Float,
    val percent: Float,
    val trend: Trend,
) {
}