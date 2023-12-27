package com.savent.erp.data.remote.model

import com.google.gson.annotations.SerializedName
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.utils.DateTimeObj

data class Movement(
    val id: Int = Integer.MAX_VALUE,
    val type: MovementType = MovementType.ALL,
    @SerializedName("employee_id")
    val employeeId: Int = 0,
    @SerializedName("reason_id")
    val reasonId: Int = 0,
    @SerializedName("provider_id")
    val providerId: Int? = null,
    @SerializedName("purchase_id")
    val purchaseId: Int? = null,
    @SerializedName("selected_products")
    val selectedProducts: HashMap<Int, Int> = HashMap(),
    @SerializedName("date_timestamp")
    val dateTimestamp: DateTimeObj = DateTimeObj.fromLong(System.currentTimeMillis()),
    @SerializedName("input_store_id")
    val inputStoreId: Int = 0,
    @SerializedName("output_store_id")
    val outputStoreId: Int = 0,
    @SerializedName("input_store_keeper_id")
    val inputStoreKeeperId: Int? = null,
    @SerializedName("output_store_keeper_id")
    val outputStoreKeeperId: Int? = null,
    val units: Int = 0,
)