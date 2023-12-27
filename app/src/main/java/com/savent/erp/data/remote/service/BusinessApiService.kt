package com.savent.erp.data.remote.service

import com.savent.erp.AppConstants
import com.savent.erp.data.remote.model.Business
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BusinessApiService {
    @GET(AppConstants.BUSINESS_API_PATH)
    suspend fun getBusiness(
        @Query("credentials") credentials: String,
        @Query("storeId") storeId: Int,
        @Query("companyId") companyId: Int
    ): Response<Business>
}