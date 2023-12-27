package com.savent.erp.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.remote.model.DebtPayment
import com.savent.erp.data.remote.service.DebtPaymentApiService
import com.savent.erp.utils.Resource

class DebtPaymentRemoteDatasourceImpl(private val debtPaymentApiService: DebtPaymentApiService) :
    DebtPaymentRemoteDatasource {

    override suspend fun insertDebtPayment(
        businessId: Int,
        sellerId: Int,
        storeId: Int,
        debtPayment: DebtPayment,
        companyId: Int
    ): Resource<Int> {
        try {
            //Log.d("log_",Gson().toJson(debtPayment))
            val response =
                debtPaymentApiService.insertDebtPayment(
                    businessId,
                    sellerId,
                    storeId,
                    Gson().toJson(debtPayment),
                    companyId
                )
            //Log.d("log_",response.toString())
            //Log.d("log_",Gson().toJson(response.body()))
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.insert_debt_payments_error)
        } catch (e: Exception) {
            //Log.d("log_",e.toString())
            return Resource.Error(message = "ConnectionError")
        }
    }

    override suspend fun getDebtPayments(storeId: Int, companyId: Int): Resource<List<DebtPayment>> {
        try {
            val response =
                debtPaymentApiService.getPayments(storeId,companyId)
            //Log.d("log_",response.toString())
            //Log.d("log_",Gson().toJson(response.body()))
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.get_debt_payments_error)
        } catch (e: Exception) {
            //Log.d("log_",e.toString())
            return Resource.Error(message = "ConnectionError")
        }
    }
}