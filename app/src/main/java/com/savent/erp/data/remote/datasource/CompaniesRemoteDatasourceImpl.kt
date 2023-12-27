package com.savent.erp.data.remote.datasource

import android.util.Log
import com.savent.erp.R
import com.savent.erp.data.remote.model.Company
import com.savent.erp.data.remote.service.CompanyApiService
import com.savent.erp.utils.Resource

class CompaniesRemoteDatasourceImpl(private val companyApiService: CompanyApiService) :
    CompaniesRemoteDatasource {

    override suspend fun getCompanies(): Resource<List<Company>> {
        try {
            val response = companyApiService.getCompanies()
            //Log.d("log_",response.toString()+response.errorBody().toString())
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.sync_company_local_data_error)
        } catch (e: Exception) {
            Log.d("log_",e.toString())
            e.printStackTrace()
            return Resource.Error(message = "Error al conectar")
        }
    }
}