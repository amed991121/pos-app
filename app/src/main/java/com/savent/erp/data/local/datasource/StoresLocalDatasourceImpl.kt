package com.savent.erp.data.local.datasource

import com.savent.erp.R
import com.savent.erp.data.local.database.dao.StoreDao
import com.savent.erp.data.local.model.StoreEntity
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class StoresLocalDatasourceImpl(private val storeDao: StoreDao) : StoresLocalDatasource() {

    override suspend fun insertStores(vararg stores: StoreEntity): Resource<Int> {
        storeDao.deleteAll()
        val result2 = storeDao.insertStores(*(stores))
        if (result2.size != stores.size)
            return Resource.Error(R.string.insert_stores_error)
        return Resource.Success()
    }

    override suspend fun updateStores(vararg stores: StoreEntity): Resource<Int> {
        val result = storeDao.updateStores(*(stores))
        if (result == 0) return Resource.Error(R.string.update_stores_error)
        return Resource.Success()
    }

    override suspend fun getStore(id: Int, companyId: Int): Resource<StoreEntity> =
        storeDao.getStore(id,companyId)?.let { Resource.Success(it) }
            ?: Resource.Error(R.string.get_stores_error)

    override suspend fun getStoreByRemoteId(remoteId: Int, companyId: Int): Resource<StoreEntity> =
        storeDao.getStoreByRemoteId(remoteId,companyId)?.let { Resource.Success(it) }
            ?: Resource.Error(R.string.get_stores_error)

    override suspend fun getStores(companyId: Int): Resource<List<StoreEntity>> =
        storeDao.getStores(companyId)?.let { Resource.Success(it) }
            ?: Resource.Error(R.string.get_stores_error)

    override fun getStores(query: String, companyId: Int): Flow<Resource<List<StoreEntity>>> = flow {
        storeDao.getStores(query,companyId).onEach {
            it?.let { it1 -> emit(Resource.Success(it1)) }
                ?: emit(Resource.Error<List<StoreEntity>>(R.string.get_stores_error))
        }.collect()
    }
}