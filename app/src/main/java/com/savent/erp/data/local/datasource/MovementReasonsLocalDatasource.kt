package com.savent.erp.data.local.datasource

import com.savent.erp.data.common.model.MovementReason
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MovementReasonsLocalDatasource {

    suspend fun insertReasons(reasons: List<MovementReason>): Resource<Int>

    suspend fun getReason(id: Int): Resource<MovementReason>

    fun getReasons(query: String, typeFilter: MovementType): Flow<Resource<List<MovementReason>>>

}