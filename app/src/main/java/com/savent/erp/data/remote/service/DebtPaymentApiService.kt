package com.savent.erp.data.remote.service

import com.savent.erp.AppConstants
import com.savent.erp.data.remote.model.DebtPayment
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DebtPaymentApiService {

    @POST(AppConstants.DEBT_PAYMENTS_API_PATH)
    suspend fun insertDebtPayment(
        @Query("businessId") businessId: Int,
        @Query("sellerId") sellerId: Int,
        @Query("storeId") storeId: Int,
        @Query("debtPayment") debtPayment: String,
        @Query("companyId") companyId: Int
    ): Response<Int>

    @GET(AppConstants.DEBT_PAYMENTS_API_PATH)
    suspend fun getPayments(
        @Query("storeId") storeId: Int,
        @Query("companyId") companyId: Int
    ): Response<List<DebtPayment>>
}