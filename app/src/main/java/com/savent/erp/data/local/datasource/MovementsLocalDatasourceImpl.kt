package com.savent.erp.data.local.datasource

import com.savent.erp.R
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.data.local.database.dao.MovementDao
import com.savent.erp.data.local.model.MovementEntity
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class MovementsLocalDatasourceImpl(private val movementDao: MovementDao) :
    MovementsLocalDatasource {

    override suspend fun insertMovement(movement: MovementEntity): Resource<Int> {
        val result = if (!areTheSame(movement)) movementDao.insertMovement(movement) else 0L
        if (result == 0L) return Resource.Error(resId = R.string.insert_movement_error)
        return Resource.Success(0)
    }

    override suspend fun insertMovements(movements: List<MovementEntity>): Resource<Int> {
        movementDao.deleteAll()
        val result = movementDao.insertMovements(movements)
        if (result.size != movements.size)
            return Resource.Error(resId = R.string.insert_movements_error)
        return Resource.Success(0)
    }

    override suspend fun getMovement(id: Int): Resource<MovementEntity> =
        Resource.Success(movementDao.getMovement(id))

    override suspend fun getMovementByRemoteId(remoteId: Int): Resource<MovementEntity> =
        Resource.Success(movementDao.getMovementByRemoteId(remoteId))

    override fun getMovements(type: MovementType): Flow<Resource<List<MovementEntity>>> =
        flow {
            val typeList =
                if (type == MovementType.ALL) listOf(
                    MovementType.INPUT,
                    MovementType.OUTPUT,
                    MovementType.ALL
                )
                else listOf(type)
            movementDao.getMovements(typeList).onEach {
                it?.let { it1 -> emit(Resource.Success(it1)) }
                    ?: emit(Resource.Error(resId = R.string.get_movements_error))
            }.collect()
        }

    override suspend fun getMovements(): Resource<List<MovementEntity>> =
        Resource.Success(movementDao.getMovements())

    override suspend fun updateMovement(movement: MovementEntity): Resource<Int> {
        val result = movementDao.updateMovement(movement)
        if (result == 0) return Resource.Error(resId = R.string.update_movement_error)
        return Resource.Success(0)
    }

    private suspend fun areTheSame(movement: MovementEntity): Boolean {
        try {
            val movementsList = getMovements(MovementType.ALL).first()
            movementsList.data?.forEach {
                val hash1 = listOf(
                    it.remoteId,
                    it.type,
                    it.employeeId,
                    it.reasonId,
                    it.providerId,
                    it.purchaseId,
                    it.inputStoreKeeperId,
                    it.outputStoreKeeperId,
                    it.storeInputId,
                    it.storeOutputId
                ).hashCode()
                val hash2 = listOf(
                    movement.remoteId,
                    movement.type,
                    movement.employeeId,
                    movement.reasonId,
                    movement.providerId,
                    movement.purchaseId,
                    movement.inputStoreKeeperId,
                    movement.outputStoreKeeperId,
                    movement.storeInputId,
                    movement.storeOutputId
                ).hashCode()
                if (hash1 == hash2) return true
            }
        } catch (e: Exception) {
            return false
        }

        return false
    }
}