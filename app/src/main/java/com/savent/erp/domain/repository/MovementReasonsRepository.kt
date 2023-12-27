package com.savent.erp.domain.repository

import com.savent.erp.data.common.model.MovementType
import com.savent.erp.data.common.model.MovementReason
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MovementReasonsRepository {

    suspend fun insertReasons(reasons: List<MovementReason>): Resource<Int>

    suspend fun getReason(id: Int): Resource<MovementReason>

    fun getMovementsReasons(
        query: String,
        typeFilter: MovementType
    ): Flow<Resource<List<MovementReason>>>

    suspend fun fetchReasons(companyId: Int): Resource<Int>
}