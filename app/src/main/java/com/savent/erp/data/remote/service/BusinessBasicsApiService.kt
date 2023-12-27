package com.savent.erp.data.remote.service

import com.savent.erp.AppConstants
import com.savent.erp.data.remote.model.BusinessBasics
import retrofit2.Response
import retrofit2.http.*

interface BusinessBasicsApiService {

    @GET(AppConstants.BUSINESS_API_PATH)
    suspend fun getBusinessBasics(
        @Query("businessId") businessId: Int,
    ): Response<BusinessBasics>

    @PUT(AppConstants.BUSINESS_API_PATH)
    suspend fun updateBasics(
        @Query("businessId") businessId: Int,
        @Body basics: BusinessBasics
    ): Response<Int>
}