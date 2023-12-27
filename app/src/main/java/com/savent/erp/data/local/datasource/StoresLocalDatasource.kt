package com.savent.erp.data.local.datasource

import com.savent.erp.data.local.model.StoreEntity
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

abstract class StoresLocalDatasource {

    abstract suspend fun insertStores(vararg stores: StoreEntity): Resource<Int>

    abstract suspend fun updateStores(vararg stores: StoreEntity): Resource<Int>

    abstract suspend fun getStore(id: Int, companyId: Int): Resource<StoreEntity>

    abstract suspend fun getStoreByRemoteId(remoteId: Int, companyId: Int): Resource<StoreEntity>

    abstract suspend fun getStores(companyId: Int): Resource<List<StoreEntity>>

    abstract fun getStores(query: String, companyId: Int): Flow<Resource<List<StoreEntity>>>

    protected fun alreadyExists(store: StoreEntity, oldData: List<StoreEntity>): Boolean {
        oldData.forEach {
            if (store.remoteId == it.remoteId) return true
        }
        return false
    }

    protected fun areContentsTheSame(store: StoreEntity, oldData: List<StoreEntity>): Boolean {
        oldData.forEach {
            if (store.hashCode() == it.hashCode()) return true
        }
        return false
    }

    protected fun prepareDataToUpdate(
        newData: List<StoreEntity>,
        oldData: List<StoreEntity>
    ): List<StoreEntity> {
        newData.forEach { newStore ->
            oldData.forEach { oldStore ->
                if (newStore.remoteId == oldStore.remoteId)
                    newStore.id = oldStore.id
            }
        }
        return newData
    }
}