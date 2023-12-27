package com.savent.erp.domain.repository

import com.savent.erp.data.local.model.ClientEntity
import com.savent.erp.utils.Resource
import com.savent.erp.data.remote.model.Client
import kotlinx.coroutines.flow.Flow

interface ClientsRepository {

    suspend fun insertClients(clients: List<Client>): Resource<Int>

    suspend fun addClient(client: Client): Resource<Int>

    suspend fun getClient(id: Int): Resource<ClientEntity>

    suspend fun getClientByRemoteId(remoteId: Int): Resource<ClientEntity>

    fun getClients(query: String): Flow<Resource<List<ClientEntity>>>

    suspend fun fetchClients(sellerId: Int, storeId: Int?, companyId: Int, category: String):
            Resource<Int>

    suspend fun updateClient(client: Client): Resource<Int>

    suspend fun deleteClient(id: Int): Resource<Int>
}