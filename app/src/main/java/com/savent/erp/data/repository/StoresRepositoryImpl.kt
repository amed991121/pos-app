package com.savent.erp.data.repository

import com.savent.erp.data.local.datasource.StoresLocalDatasource
import com.savent.erp.data.local.model.StoreEntity
import com.savent.erp.data.remote.datasource.StoresRemoteDatasource
import com.savent.erp.data.remote.model.Store
import com.savent.erp.domain.repository.StoresRepository
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class StoresRepositoryImpl(
    private val localDataSource: StoresLocalDatasource,
    private val remoteDatasource: StoresRemoteDatasource,
    private val mapper: (Store) -> StoreEntity
): StoresRepository {
    override suspend fun insertStores(vararg stores: Store): Resource<Int> =
        localDataSource.insertStores(*(stores.map { mapper(it) }.toTypedArray()))

    override suspend fun getStore(id: Int, companyId: Int): Resource<StoreEntity> =
        localDataSource.getStore(id, companyId)

    override suspend fun getStoreByRemoteId(remoteId: Int, companyId: Int): Resource<StoreEntity> =
        localDataSource.getStoreByRemoteId(remoteId, companyId)

    override suspend fun getStores(companyId: Int): Resource<List<StoreEntity>> =
        localDataSource.getStores(companyId)

    override fun getStores(query: String, companyId: Int): Flow<Resource<List<StoreEntity>>> = flow{
        localDataSource.getStores(query, companyId).onEach { emit(it) }.collect()
    }

    override suspend fun fetchStores(companyId: Int): Resource<Int> {
        val response = remoteDatasource.getStores(companyId)
        if (response is Resource.Success) {
            response.data?.let {
                //Log.d("log_","response"+Gson().toJson(it))
                insertStores(*(it.toTypedArray()))
            }
            return Resource.Success()
        }
        return Resource.Error(resId = response.resId, message = response.message)
    }
}