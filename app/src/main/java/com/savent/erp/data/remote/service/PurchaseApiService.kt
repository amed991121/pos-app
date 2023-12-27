package com.savent.erp.data.remote.service

import com.savent.erp.AppConstants
import com.savent.erp.data.common.model.Purchase
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PurchaseApiService {
    @GET(AppConstants.PURCHASES_API_PATH)
    suspend fun getPurchases(
        @Query("companyId") companyId: Int
    ): Response<List<Purchase>>
}