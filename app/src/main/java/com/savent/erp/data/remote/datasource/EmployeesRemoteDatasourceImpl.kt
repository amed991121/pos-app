package com.savent.erp.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.common.model.Employee
import com.savent.erp.data.remote.service.EmployeeApiService
import com.savent.erp.utils.Resource

class EmployeesRemoteDatasourceImpl(private val employeeApiService: EmployeeApiService) :
    EmployeesRemoteDataSource {

    override suspend fun getEmployees(companyId: Int): Resource<List<Employee>> {
        try {
            val response = employeeApiService.getEmployees(companyId)
            //Log.d("log_",response.toString())
            //Log.d("log_", Gson().toJson(response.body()))
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.get_employees_error)
        } catch (e: Exception) {
            //Log.d("log_",e.toString())
            return Resource.Error(message = "ConnectionError")
        }
    }
}