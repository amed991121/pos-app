package com.savent.erp.data.remote.service

import com.savent.erp.AppConstants
import com.savent.erp.data.remote.model.Store
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StoreApiService {
    @GET(AppConstants.STORES_API_PATH)
    suspend fun getStores(@Query("companyId") companyId: Int): Response<List<Store>>
}