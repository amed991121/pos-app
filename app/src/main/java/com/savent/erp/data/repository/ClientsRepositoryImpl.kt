package com.savent.erp.data.repository

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.data.local.datasource.ClientsLocalDatasource
import com.savent.erp.data.local.model.ClientEntity
import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.data.remote.datasource.ClientsRemoteDatasource
import com.savent.erp.data.remote.model.Client
import com.savent.erp.data.remote.model.Product
import com.savent.erp.domain.repository.ClientsRepository
import com.savent.erp.utils.DateFormat
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

class ClientsRepositoryImpl(
    private val localDatasource: ClientsLocalDatasource,
    private val remoteDatasource: ClientsRemoteDatasource
) : ClientsRepository {

    override suspend fun insertClients(clients: List<Client>): Resource<Int> =
        localDatasource.insertClients(clients.map {
            mapToEntity(
                it,
                PendingRemoteAction.COMPLETED
            )
        })

    override suspend fun addClient(client: Client): Resource<Int> =
        localDatasource.addClient(mapToEntity(client, PendingRemoteAction.COMPLETED))

    override suspend fun getClient(id: Int): Resource<ClientEntity> =
        localDatasource.getClient(id)

    override suspend fun getClientByRemoteId(remoteId: Int): Resource<ClientEntity> =
        localDatasource.getClientByRemoteId(remoteId)

    override fun getClients(query: String): Flow<Resource<List<ClientEntity>>> = flow {
        localDatasource.getClients(query).onEach { emit(it) }.collect()
    }

    override suspend fun fetchClients(
        sellerId: Int,
        storeId: Int?,
        companyId: Int,
        category: String
    ): Resource<Int> {
        //Log.d("log_","fetchClients")
        val response = remoteDatasource.getClients(sellerId, storeId, companyId, category)

        if (response is Resource.Success) {
            response.data?.let {
                //Log.d("log_","response"+Gson().toJson(it))
                insertClients(it)
            }
            return Resource.Success()
        }
        return Resource.Error(resId = response.resId, message = response.message)
    }

    override suspend fun updateClient(client: Client): Resource<Int> =
        localDatasource.updateClient(mapToEntity(client, PendingRemoteAction.UPDATE))

    override suspend fun deleteClient(id: Int): Resource<Int> =
        localDatasource.deleteClient(id, PendingRemoteAction.DELETE)

    private fun mapToEntity(client: Client, actionPending: PendingRemoteAction): ClientEntity {
        return ClientEntity(
            0,
            client.id ?: 0,
            client.image,
            client.name,
            client.tradeName,
            client.paternalName?: "",
            client.maternalName?: "",
            client.socialReason?: "",
            client.rfc?: "",
            client.phoneNumber,
            client.email?: "",
            client.street?: "",
            client.noExterior?: "",
            client.colonia?: "",
            client.postalCode?: "",
            client.city?: "",
            client.state?: "",
            client.country?: "",
            client.location,
            client.creditLimit,
            System.currentTimeMillis(),
            actionPending
        )
    }


}