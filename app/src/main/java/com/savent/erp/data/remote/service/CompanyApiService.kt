package com.savent.erp.data.remote.service

import com.savent.erp.AppConstants
import com.savent.erp.data.remote.model.Company
import retrofit2.Response
import retrofit2.http.GET

interface CompanyApiService {

    @GET(AppConstants.COMPANIES_API_PATH)
    suspend fun getCompanies(): Response<List<Company>>

}