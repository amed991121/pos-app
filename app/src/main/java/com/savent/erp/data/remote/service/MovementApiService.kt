package com.savent.erp.data.remote.service

import com.savent.erp.AppConstants
import com.savent.erp.data.remote.model.Movement
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MovementApiService {

    @POST(AppConstants.MOVEMENTS_API_PATH)
    suspend fun insertMovement(
        @Query("businessId") businessId: Int,
        @Query("movement") movement: String,
        @Query("companyId") companyId: Int
    ): Response<Int>

    @GET(AppConstants.MOVEMENTS_API_PATH)
    suspend fun getMovements(
        @Query("businessId") businessId: Int,
        @Query("storeId") storeId: Int,
        @Query("companyId") companyId: Int
    ): Response<List<Movement>>
}