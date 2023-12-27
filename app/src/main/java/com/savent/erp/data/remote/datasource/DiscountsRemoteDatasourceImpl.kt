package com.savent.erp.data.remote.datasource

import android.util.Log
import com.savent.erp.R
import com.savent.erp.data.common.model.Discount
import com.savent.erp.data.remote.service.DiscountApiService
import com.savent.erp.utils.Resource

class DiscountsRemoteDatasourceImpl(private val discountApiService: DiscountApiService) :
    DiscountsRemoteDatasource {

    override suspend fun getDiscounts(
        storeId: Int,
        clientId: Int
    ): Resource<List<Discount>> {
        try {
            val response =
                discountApiService.getDiscounts(storeId, clientId)
            if (response.isSuccessful)
                return Resource.Success(response.body())
            if(response.code() == 404)
                return Resource.Error(resId = R.string.discounts_not_found)
            return Resource.Error(resId = R.string.get_discounts_error)
        } catch (e: Exception) {
            return Resource.Error(resId = R.string.sync_input_error, message = "Error al conectar")
        }
    }
}