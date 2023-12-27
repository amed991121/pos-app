package com.savent.erp.data.remote.datasource

import com.savent.erp.data.common.model.MovementReason
import com.savent.erp.utils.Resource

interface MovementReasonsRemoteDatasource {
    suspend fun getReasons(
        companyId: Int
    ): Resource<List<MovementReason>>
}