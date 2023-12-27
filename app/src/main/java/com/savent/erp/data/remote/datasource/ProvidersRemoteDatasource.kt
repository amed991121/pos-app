package com.savent.erp.data.remote.datasource

import com.savent.erp.data.common.model.Provider
import com.savent.erp.utils.Resource

interface ProvidersRemoteDatasource {
    suspend fun getProviders(
        companyId: Int
    ): Resource<List<Provider>>
}