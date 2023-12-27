package com.savent.erp.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.common.model.Purchase
import com.savent.erp.data.remote.service.PurchaseApiService
import com.savent.erp.utils.Resource

class PurchaseRemoteDatasourceImpl(private val purchaseApiService: PurchaseApiService) :
    PurchaseRemoteDatasource {

    override suspend fun getPurchases(companyId: Int): Resource<List<Purchase>> {
        try {
            val response = purchaseApiService.getPurchases(companyId)
            //Log.d("log_",response.toString())
            //Log.d("log_", Gson().toJson(response.body()))
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.get_purchases_error)
        } catch (e: Exception) {
            //Log.d("log_",e.toString())
            return Resource.Error(message = "ConnectionError")
        }
    }
}