package com.savent.erp.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.common.model.MovementReason
import com.savent.erp.data.remote.service.MovementReasonApiService
import com.savent.erp.utils.Resource

class MovementReasonsRemoteDatasourceImpl(
    private val movementReasonApiService: MovementReasonApiService
) : MovementReasonsRemoteDatasource {
    override suspend fun getReasons(
        companyId: Int
    ): Resource<List<MovementReason>> {
        try {
            val response = movementReasonApiService.getReasons(companyId)
            Log.d("log_",response.toString())
            Log.d("log_", Gson().toJson(response.body()))
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.get_reasons_error)
        } catch (e: Exception) {
            Log.d("log_",e.toString())
            return Resource.Error(message = "ConnectionError")
        }
    }
}