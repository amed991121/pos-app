package com.savent.erp.data.remote.service

import com.savent.erp.AppConstants
import com.savent.erp.data.common.model.Discount
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DiscountApiService {

    @GET(AppConstants.DISCOUNTS_API_PATH)
    suspend fun getDiscounts(
        @Query("storeId") storeId: Int,
        @Query("clientId") clientId: Int
    ): Response<List<Discount>>

}