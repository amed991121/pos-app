package com.savent.erp.data.local.datasource

import com.savent.erp.R
import com.savent.erp.data.common.model.MovementReason
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.data.local.database.dao.MovementReasonDao
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class MovementReasonsLocalDatasourceImpl(private val movementReasonDao: MovementReasonDao) :
    MovementReasonsLocalDatasource {
    override suspend fun insertReasons(reasons: List<MovementReason>): Resource<Int> {
        movementReasonDao.deleteAll()
        val result = movementReasonDao.insertReasons(reasons)
        if (result.size != reasons.size)
            return Resource.Error(resId = R.string.insert_reasons_error)
        return Resource.Success()
    }

    override suspend fun getReason(id: Int): Resource<MovementReason> =
        Resource.Success(movementReasonDao.getReason(id))

    override fun getReasons(query: String, typeFilter: MovementType): Flow<Resource<List<MovementReason>>> = flow {
        movementReasonDao.getReasons(query, typeFilter).onEach {
            it?.let { it1 -> emit(Resource.Success(it1)) }
                ?: emit(Resource.Error(resId = R.string.get_reasons_error))
        }.collect()
    }
}