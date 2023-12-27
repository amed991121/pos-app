package com.savent.erp.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.remote.model.Movement
import com.savent.erp.data.remote.service.MovementApiService
import com.savent.erp.utils.Resource

class MovementsRemoteDatasourceImpl(private val movementApiService: MovementApiService) :
    MovementsRemoteDatasource {

    override suspend fun insertMovement(
        businessId: Int,
        movement: Movement,
        companyId: Int
    ): Resource<Int> {
        try {
            Log.d("log_",Gson().toJson(movement))
            val response =
                movementApiService.insertMovement(
                    businessId,
                    Gson().toJson(movement),
                    companyId
                )
            Log.d("log_",response.toString())
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.insert_movement_error)
        } catch (e: Exception) {
            Log.d("log_","exInsertSale"+e.toString())
            return Resource.Error(message = "ConnectionError")
        }
    }

    override suspend fun getMovements(
        businessId: Int,
        storeId: Int,
        companyId: Int
    ): Resource<List<Movement>> {
        try {
            val response = movementApiService.getMovements(businessId, storeId, companyId)
            Log.d("log_",response.toString())
            Log.d("log_",Gson().toJson(response.body()))
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.get_movements_error)
        } catch (e: Exception) {
            Log.d("log_",e.toString())
            return Resource.Error(message = "ConnectionError")
        }
    }
}