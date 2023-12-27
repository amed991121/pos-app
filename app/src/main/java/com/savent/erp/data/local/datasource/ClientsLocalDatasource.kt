package com.savent.erp.data.local.datasource

import com.savent.erp.data.local.model.ClientEntity
import com.savent.erp.utils.Resource
import com.savent.erp.data.remote.model.Client
import com.savent.erp.utils.PendingRemoteAction
import kotlinx.coroutines.flow.Flow

interface ClientsLocalDatasource {

    suspend fun insertClients(clients: List<ClientEntity>): Resource<Int>

    suspend fun addClient(client: ClientEntity): Resource<Int>

    suspend fun getClient(id:Int): Resource<ClientEntity>

    suspend fun getClientByRemoteId(remoteId: Int): Resource<ClientEntity>

    fun getClients(): Flow<Resource<List<ClientEntity>>>

    fun getClients(query: String): Flow<Resource<List<ClientEntity>>>

    suspend fun updateClient(client: ClientEntity): Resource<Int>

    suspend fun updateClients(clients: List<ClientEntity>): Resource<Int>

    suspend fun deleteClient(id: Int, action: PendingRemoteAction): Resource<Int>

    suspend fun deleteAllClients(): Resource<Int>


}