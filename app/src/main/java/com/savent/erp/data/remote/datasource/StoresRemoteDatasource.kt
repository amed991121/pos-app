package com.savent.erp.data.remote.datasource

import com.savent.erp.data.remote.model.Store
import com.savent.erp.utils.Resource

interface StoresRemoteDatasource {
    suspend fun getStores(companyId: Int): Resource<List<Store>>
}