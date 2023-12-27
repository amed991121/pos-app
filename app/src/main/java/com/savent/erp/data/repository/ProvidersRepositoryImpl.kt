package com.savent.erp.data.repository

import com.savent.erp.data.common.model.Provider
import com.savent.erp.data.local.datasource.ProvidersLocalDatasource
import com.savent.erp.data.remote.datasource.ProvidersRemoteDatasource
import com.savent.erp.domain.repository.ProvidersRepository
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class ProvidersRepositoryImpl(
    private val localDatasource: ProvidersLocalDatasource,
    private val remoteDatasource: ProvidersRemoteDatasource
): ProvidersRepository {

    override suspend fun insertProviders(providers: List<Provider>): Resource<Int> =
        localDatasource.insertProviders(providers)

    override suspend fun getProvider(id: Int): Resource<Provider> =
        localDatasource.getProvider(id)

    override fun getProviders(query: String): Flow<Resource<List<Provider>>> = flow {
        localDatasource.getProviders(query).onEach { emit(it) }.collect()
    }

    override suspend fun fetchProviders(companyId: Int): Resource<Int> {
        val response = remoteDatasource.getProviders(companyId)
        if (response is Resource.Error || response.data == null) return Resource.Error(
            resId = response.resId,
            message = response.message
        )
        return localDatasource.insertProviders(response.data)
    }

}