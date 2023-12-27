package com.savent.erp.data.remote.service

import com.savent.erp.AppConstants
import com.savent.erp.data.common.model.Employee
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EmployeeApiService {

    @GET(AppConstants.EMPLOYEES_API_PATH)
    suspend fun getEmployees(
        @Query("companyId") companyId: Int
    ): Response<List<Employee>>

}