package com.savent.erp.data.remote.model

import com.google.gson.annotations.SerializedName

data class Store(
    val id: Int,
    val name: String,
    @SerializedName("company_id")
    val companyId: Int
)