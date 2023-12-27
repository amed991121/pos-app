package com.savent.erp.data.remote.service

import com.savent.erp.AppConstants
import com.savent.erp.data.common.model.Provider
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProviderApiService {

    @GET(AppConstants.PROVIDERS_API_PATH)
    suspend fun getProviders(
        @Query("companyId") companyId: Int
    ): Response<List<Provider>>
}