package com.savent.erp.data.local.datasource

import com.savent.erp.R
import com.savent.erp.data.common.model.Provider
import com.savent.erp.data.local.database.dao.ProviderDao
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class ProvidersLocalDatasourceImpl(private val providerDao: ProviderDao): ProvidersLocalDatasource {
    override suspend fun insertProviders(providers: List<Provider>): Resource<Int> {
        providerDao.deleteAll()
        val result = providerDao.insertProviders(providers)
        if (result.size != providers.size)
            return Resource.Error(resId = R.string.insert_providers_error)
        return Resource.Success()
    }

    override suspend fun getProvider(id: Int): Resource<Provider> =
        Resource.Success(providerDao.getProvider(id))

    override fun getProviders(query: String): Flow<Resource<List<Provider>>> = flow {
        providerDao.getProviders(query).onEach {
            it?.let { it1 -> emit(Resource.Success(it1)) }
                ?: emit(Resource.Error(resId = R.string.get_providers_error))
        }.collect()
    }

}