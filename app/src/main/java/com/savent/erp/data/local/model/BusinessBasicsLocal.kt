package com.savent.erp.data.local.model

import com.google.android.gms.maps.model.LatLng
import com.savent.erp.utils.PendingRemoteAction

class BusinessBasicsLocal(
    val id: Int,
    val name: String,
    val sellerId: Int,
    val sellerName: String,
    val sellerLevel: Int,
    val storeId: Int,
    val storeName: String,
    val companyId: Int,
    val location: LatLng,
    val address: String,
    val image: String?,
    val receiptFooter: String?,
    var remoteAction: PendingRemoteAction
) {
}