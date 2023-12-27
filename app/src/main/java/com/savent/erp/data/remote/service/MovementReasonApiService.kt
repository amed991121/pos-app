package com.savent.erp.data.remote.service

import com.savent.erp.AppConstants
import com.savent.erp.data.common.model.MovementReason
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovementReasonApiService {

    @GET(AppConstants.MOVEMENT_REASONS_API_PATH)
    suspend fun getReasons(
        @Query("companyId") companyId: Int
    ): Response<List<MovementReason>>

}