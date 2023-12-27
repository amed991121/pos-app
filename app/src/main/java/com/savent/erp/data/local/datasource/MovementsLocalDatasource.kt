package com.savent.erp.data.local.datasource

import com.savent.erp.data.common.model.MovementType
import com.savent.erp.data.local.model.MovementEntity
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MovementsLocalDatasource {

    suspend fun insertMovement(movement: MovementEntity): Resource<Int>

    suspend fun insertMovements(movements: List<MovementEntity>): Resource<Int>

    suspend fun getMovement(id: Int): Resource<MovementEntity>

    suspend fun getMovementByRemoteId(remoteId: Int): Resource<MovementEntity>

    suspend fun getMovements(): Resource<List<MovementEntity>>

    fun getMovements(type: MovementType): Flow<Resource<List<MovementEntity>>>

    suspend fun updateMovement(movement: MovementEntity): Resource<Int>

}