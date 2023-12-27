package com.savent.erp.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.remote.model.Sale
import com.savent.erp.data.remote.service.SaleApiService
import com.savent.erp.utils.Resource

class SalesRemoteDatasourceImpl(private val saleApiService: SaleApiService) :
    SalesRemoteDatasource {

    override suspend fun insertSale(
        businessId: Int,
        sellerId: Int,
        storeId: Int,
        sale: Sale,
        companyId: Int
    ):
            Resource<Int> {
        try {
            //Log.d("log_",Gson().toJson(sale))
            val response =
                saleApiService.insertSale(
                    businessId,
                    sellerId,
                    storeId,
                    Gson().toJson(sale),
                    companyId
                )
            //Log.d("log_",response.toString())
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.insert_sale_error)
        } catch (e: Exception) {
            //Log.d("log_","exInsertSale"+e.toString())
            return Resource.Error(message = "ConnectionError")
        }

    }

    override suspend fun getSales(
        businessId: Int,
        storeId: Int,
        date: String,
        companyId: Int
    ):
            Resource<List<Sale>> {
        try {
            val response = saleApiService.getSales(businessId, storeId, date, companyId)
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.get_sales_error)
        } catch (e: Exception) {
            return Resource.Error(message = "ConnectionError")
        }
    }


}