package com.savent.erp.data.remote.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

class BusinessBasics (
    val id: Int,
    val name: String,
    @SerializedName("seller_id")
    val sellerId: Int,
    @SerializedName("seller_name")
    val sellerName: String,
    @SerializedName("seller_level")
    val sellerLevel: Int,
    @SerializedName("store_id")
    val storeId: Int,
    @SerializedName("store_name")
    val storeName: String,
    @SerializedName("company_id")
    val companyId: Int,
    val location: LatLng,
    val address: String,
    val image: String?,
    @SerializedName("receipt_footer")
    val receiptFooter: String?,
)