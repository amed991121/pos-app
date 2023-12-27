package com.savent.erp.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.savent.erp.utils.PendingRemoteAction

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "remote_id")
    var remoteId: Int,
    @ColumnInfo(name = "image")
    val image: String?,
    val name: String?,
    @ColumnInfo(name = "trade_name")
    val tradeName: String?,
    @ColumnInfo(name = "paternal")
    val paternalName: String?,
    @ColumnInfo(name = "maternal")
    val maternalName: String?,
    @ColumnInfo(name = "social_reason")
    val socialReason: String?,
    val rfc: String?,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: Long?,
    val email: String?,
    val street: String?,
    @ColumnInfo(name = "no_exterior")
    val noExterior: String?,
    val colonia: String?,
    @ColumnInfo(name = "postal_code")
    val postalCode: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val location: LatLng?,
    @ColumnInfo(name = "credit_limit")
    val creditLimit: Float?,
    @ColumnInfo(name = "date_timestamp")
    val dateTimestamp: Long?,
    @ColumnInfo(name = "remote_action")
    var pendingRemoteAction: PendingRemoteAction
) {
}