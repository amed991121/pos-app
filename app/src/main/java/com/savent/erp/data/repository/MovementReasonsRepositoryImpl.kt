package com.savent.erp.data.repository

import com.savent.erp.data.common.model.MovementType
import com.savent.erp.data.common.model.MovementReason
import com.savent.erp.data.local.datasource.MovementReasonsLocalDatasource
import com.savent.erp.data.remote.datasource.MovementReasonsRemoteDatasource
import com.savent.erp.domain.repository.MovementReasonsRepository
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class MovementReasonsRepositoryImpl(
    private val localDatasource: MovementReasonsLocalDatasource,
    private val remoteDatasource: MovementReasonsRemoteDatasource
) : MovementReasonsRepository {

    override suspend fun insertReasons(reasons: List<MovementReason>): Resource<Int> =
        localDatasource.insertReasons(reasons)

    override suspend fun getReason(id: Int): Resource<MovementReason> =
        localDatasource.getReason(id)

    override fun getMovementsReasons(
        query: String,
        typeFilter: MovementType
    ): Flow<Resource<List<MovementReason>>> = flow {
        localDatasource.getReasons(query, typeFilter).onEach { emit(it) }.collect()
    }

    override suspend fun fetchReasons(companyId: Int): Resource<Int> {
        val response = remoteDatasource.getReasons(companyId)
        if (response is Resource.Error || response.data == null)
            return Resource.Error(
                resId = response.resId,
                message = response.message
            )
        return localDatasource.insertReasons(reasons = response.data)
    }
}