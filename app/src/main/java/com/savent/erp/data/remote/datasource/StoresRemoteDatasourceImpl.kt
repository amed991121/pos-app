package com.savent.erp.data.remote.datasource

import android.util.Log
import com.savent.erp.R
import com.savent.erp.data.remote.model.Store
import com.savent.erp.data.remote.service.StoreApiService
import com.savent.erp.utils.Resource

class StoresRemoteDatasourceImpl(private val storeApiService: StoreApiService) :
    StoresRemoteDatasource {

    override suspend fun getStores(companyId: Int): Resource<List<Store>> {
        try {
            val response = storeApiService.getStores(companyId)
            //Log.d("log_",response.toString()+response.errorBody().toString())
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.sync_store_local_data_error)
        } catch (e: Exception) {
            //Log.d("log_",e.toString())
            return Resource.Error(message = "Error al conectar")
        }
    }
}