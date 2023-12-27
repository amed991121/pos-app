package com.savent.erp.domain.repository

import com.savent.erp.data.common.model.MovementType
import com.savent.erp.data.local.model.MovementEntity
import com.savent.erp.data.remote.model.Movement
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MovementsRepository {

    suspend fun registerMovement(
        businessId: Int,
        movement: Movement,
        companyId: Int
    ): Resource<Int>

    suspend fun insertMovements(movements: List<Movement>): Resource<Int>

    suspend fun getMovement(id: Int): Resource<MovementEntity>

    suspend fun getMovementByRemoteId(remoteId: Int): Resource<MovementEntity>

    fun getMovements(type: MovementType): Flow<Resource<List<MovementEntity>>>

    suspend fun fetchMovements(
        businessId: Int,
        storeId: Int,
        companyId: Int
    ): Resource<Int>

    suspend fun syncOutputData(businessId: Int,companyId: Int): Resource<Int>

    fun isPendingOutputSync(): Flow<Resource<Boolean>>

}