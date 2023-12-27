package com.savent.erp.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.common.model.Provider
import com.savent.erp.data.remote.service.ProviderApiService
import com.savent.erp.utils.Resource

class ProvidersRemoteDatasourceImpl(private val providerApiService: ProviderApiService) :
    ProvidersRemoteDatasource {

    override suspend fun getProviders(companyId: Int): Resource<List<Provider>> {
        try {
            val response = providerApiService.getProviders(companyId)
            //Log.d("log_",response.toString())
            //Log.d("log_", Gson().toJson(response.body()))
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.get_providers_error)
        } catch (e: Exception) {
            //Log.d("log_",e.toString())
            return Resource.Error(message = "ConnectionError")
        }
    }
}