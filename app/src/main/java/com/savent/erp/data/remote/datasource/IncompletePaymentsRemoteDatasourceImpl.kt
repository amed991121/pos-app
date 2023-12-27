package com.savent.erp.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.remote.model.IncompletePayment
import com.savent.erp.data.remote.service.IncompletePaymentApiService
import com.savent.erp.utils.Resource

class IncompletePaymentsRemoteDatasourceImpl(
    private val incompletePaymentApiService: IncompletePaymentApiService
) : IncompletePaymentsRemoteDatasource {

    override suspend fun getIncompletePayments(businessId: Int, storeId: Int, companyId: Int):
            Resource<List<IncompletePayment>> {
        try {
            val response =
                incompletePaymentApiService.getIncompletePayments(
                    businessId,
                    storeId,
                    null,
                    companyId
                )
            //Log.d("log_",response.toString())
            //Log.d("log_",Gson().toJson(response.body()))
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.get_incomplete_payments_error)
        } catch (e: Exception) {
            //Log.d("log_",e.toString())
            return Resource.Error(resId = R.string.sync_input_error, message = "Error al conectar")
        }
    }

}