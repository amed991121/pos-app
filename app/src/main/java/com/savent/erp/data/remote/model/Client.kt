package com.savent.erp.data.remote.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.savent.erp.utils.DateTimeObj

data class Client(
    val id: Int?,
    val image: String?,
    val name: String?,
    @SerializedName("trade_name")
    val tradeName: String?,
    @SerializedName("paternal")
    val paternalName: String?,
    @SerializedName("maternal")
    val maternalName: String?,
    @SerializedName("social_reason")
    val socialReason: String?,
    val rfc: String?,
    @SerializedName("phone_number")
    val phoneNumber: Long?,
    val email: String?,
    val street: String?,
    @SerializedName("no_exterior")
    val noExterior: String?,
    val colonia: String?,
    @SerializedName("postal_code")
    val postalCode: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val location: LatLng?,
    @SerializedName("credit_limit")
    val creditLimit: Float?,
    @SerializedName("date_timestamp")
    val dateTimestamp: DateTimeObj?,
) {
}