package com.savent.erp.data.remote.datasource

import com.savent.erp.data.remote.model.Movement
import com.savent.erp.utils.Resource

interface MovementsRemoteDatasource {

    suspend fun insertMovement(
        businessId: Int,
        movement: Movement,
        companyId: Int
    ): Resource<Int>

    suspend fun getMovements(
        businessId: Int,
        storeId: Int,
        companyId: Int
    ): Resource<List<Movement>>

}