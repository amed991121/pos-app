package com.savent.erp.data.remote.service

import com.savent.erp.AppConstants
import com.savent.erp.data.remote.model.IncompletePayment
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IncompletePaymentApiService {

    @GET(AppConstants.INCOMPLETE_PAYMENTS_API_PATH)
    suspend fun getIncompletePayments(
        @Query("businessId") businessId: Int,
        @Query("storeId") storeId: Int,
        @Query("clientId") clientId: Int?,
        @Query("companyId") companyId: Int
    ): Response<List<IncompletePayment>>


}