package com.savent.erp.domain.repository

import com.savent.erp.data.local.model.StoreEntity
import com.savent.erp.data.remote.model.Store
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface StoresRepository {

    suspend fun insertStores(vararg stores: Store): Resource<Int>

    suspend fun getStore(id: Int, companyId: Int): Resource<StoreEntity>

    suspend fun getStoreByRemoteId(remoteId: Int, companyId: Int): Resource<StoreEntity>

    suspend fun getStores(companyId: Int): Resource<List<StoreEntity>>

    fun getStores(query: String, companyId: Int): Flow<Resource<List<StoreEntity>>>

    suspend fun fetchStores(companyId: Int): Resource<Int>
}