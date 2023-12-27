package com.savent.erp.domain.repository

import com.savent.erp.data.common.model.Provider
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ProvidersRepository {

    suspend fun insertProviders(providers: List<Provider>): Resource<Int>

    suspend fun getProvider(id: Int): Resource<Provider>

    fun getProviders(query: String): Flow<Resource<List<Provider>>>

    suspend fun fetchProviders(companyId: Int): Resource<Int>

}