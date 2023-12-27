package com.savent.erp.data.repository

import android.util.Log
import com.savent.erp.R
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.data.local.datasource.MovementsLocalDatasource
import com.savent.erp.data.local.model.MovementEntity
import com.savent.erp.data.remote.datasource.MovementsRemoteDatasource
import com.savent.erp.data.remote.model.Movement
import com.savent.erp.domain.repository.MovementsRepository
import com.savent.erp.utils.IsFromToday
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.RepoMapper
import com.savent.erp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

class MovementsRepositoryImpl(
    private val localDatasource: MovementsLocalDatasource,
    private val remoteDatasource: MovementsRemoteDatasource,
    private val mappers: RepoMapper<MovementEntity, Movement>
) : MovementsRepository {

    override suspend fun registerMovement(
        businessId: Int,
        movement: Movement,
        companyId: Int
    ): Resource<Int> {
        val movementEntity =
            mappers.local(movement).copy(pendingRemoteAction = PendingRemoteAction.INSERT)
        val result = localDatasource.insertMovement(movementEntity)
        if (result is Resource.Error) return result
        val response = remoteDatasource.insertMovement(businessId, movement, companyId)
        if (response is Resource.Success)
            localDatasource.updateMovement(
                movementEntity.copy(pendingRemoteAction = PendingRemoteAction.COMPLETED)
            )
        return Resource.Success(0)
    }


    override suspend fun insertMovements(movements: List<Movement>): Resource<Int> =
        localDatasource.insertMovements(movements.map(mappers.local))

    override suspend fun getMovement(id: Int): Resource<MovementEntity> =
        localDatasource.getMovement(id)

    override suspend fun getMovementByRemoteId(remoteId: Int): Resource<MovementEntity> =
        localDatasource.getMovementByRemoteId(remoteId)

    override fun getMovements(type: MovementType): Flow<Resource<List<MovementEntity>>> =
        flow {
            localDatasource.getMovements(type).onEach { emit(it) }.collect()
        }

    override suspend fun fetchMovements(
        businessId: Int,
        storeId: Int,
        companyId: Int
    ): Resource<Int> {
        val response = remoteDatasource.getMovements(businessId, storeId, companyId)
        if (response is Resource.Error || response.data == null) return Resource.Error(
            resId = response.resId,
            message = response.message
        )
        return localDatasource.insertMovements(response.data.map(mappers.local))
    }

    override suspend fun syncOutputData(businessId: Int, companyId: Int): Resource<Int> =
        synchronized(this) {
            runBlocking(Dispatchers.IO) {
                val movements =
                    localDatasource.getMovements().let {
                        if (it is Resource.Error) return@runBlocking Resource.Error(
                            resId = it.resId,
                            message = it.message
                        )
                        else it.data
                    }
                        ?: kotlin.run {
                            return@runBlocking Resource.Error(resId = R.string.get_movements_error)
                        }

                val pendingTransactions = movements.filter { movementEntity ->
                    movementEntity.pendingRemoteAction != PendingRemoteAction.COMPLETED
                            && IsFromToday(movementEntity.dateTimestamp)
                }
                pendingTransactions.forEach { movementEntity ->
                    //Log.d("log_", "remoteMove Inserting: $movementEntity")
                    val response = remoteDatasource.insertMovement(
                        businessId,
                        mappers.network(movementEntity),
                        companyId
                    )
                    //Log.d("log_", "response: ${response.data}")
                    if (response is Resource.Success)
                        localDatasource.updateMovement(
                            movementEntity.copy(
                                remoteId = response.data ?: 0,
                                pendingRemoteAction = PendingRemoteAction.COMPLETED
                            )
                        )
                }

                if (isPendingOutputSync().first().data == false)
                    Resource.Error<Int>(R.string.sync_movements_error)

                Resource.Success(0)

            }
        }


    override fun isPendingOutputSync(): Flow<Resource<Boolean>> = flow {
        localDatasource.getMovements(MovementType.ALL).onEach { resource ->
            if (resource is Resource.Error || resource.data == null)
                emit(Resource.Error(resId = R.string.get_movements_error))
            else {
                val movements = resource.data
                emit(
                    Resource.Success(movements.any { movementEntity ->
                        movementEntity.pendingRemoteAction != PendingRemoteAction.COMPLETED && IsFromToday(
                            movementEntity.dateTimestamp
                        )
                    })
                )
            }
        }.collect()


    }
}