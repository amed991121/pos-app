package com.savent.erp.data.local.datasource

import com.savent.erp.R
import com.savent.erp.data.local.database.dao.ClientDao
import com.savent.erp.data.local.model.ClientEntity
import com.savent.erp.utils.DateFormat
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class  ClientsLocalDatasourceImpl(
    private val clientDao: ClientDao
) : ClientsLocalDatasource {

    override suspend fun insertClients(clients: List<ClientEntity>): Resource<Int> {
        val newClients = clients.filter { !areTheSame(it) }
        val toInsert = newClients.filter { !alreadyExists(it.remoteId) }
        val toUpdate = preparingNewClientsToUpdate(newClients.minus(toInsert))
        val result1 = clientDao.insertClients(toInsert)
        val result2 = clientDao.updateClients(toUpdate)
        if (result1.size != toInsert.size && result2 != toUpdate.size)
            return Resource.Error(resId = R.string.insert_clients_error)
        return Resource.Success()
    }

    override suspend fun addClient(client: ClientEntity): Resource<Int> {
        val result = if (!areTheSame(client)) clientDao.addClient(client) else 0L
        if (result == 0L) return Resource.Error(resId = R.string.add_client_error)
        return Resource.Success()
    }

    override suspend fun getClient(id: Int): Resource<ClientEntity> {
        return try {
            Resource.Success(clientDao.getClient(id))
        } catch (e: Exception) {
            //Log.d("log_", e.toString())
            Resource.Error(resId = R.string.get_clients_error)
        }

    }

    override suspend fun getClientByRemoteId(remoteId: Int): Resource<ClientEntity> {
        return try {
            Resource.Success(clientDao.getClientByRemoteId(remoteId))
        } catch (e: Exception) {
            ///Log.d("log_", e.toString())
            Resource.Error(resId = R.string.get_clients_error)
        }
    }

    override fun getClients(): Flow<Resource<List<ClientEntity>>> = flow {
        clientDao.getClients().onEach {
            it?.let { it1 -> emit(Resource.Success(it1)) }
                ?: emit(Resource.Error<List<ClientEntity>>(resId = R.string.get_clients_error))
        }.collect()
    }

    override fun getClients(query: String): Flow<Resource<List<ClientEntity>>> = flow {
        clientDao.getClients(query).onEach {
            it?.let { it1 -> emit(Resource.Success(it1)) }
                ?: emit(Resource.Error<List<ClientEntity>>(resId = R.string.get_clients_error))
        }.collect()
    }

    override suspend fun updateClient(client: ClientEntity): Resource<Int> {
        val result = clientDao.updateClient(client)
        if (result == 0) return Resource.Error(resId = R.string.update_client_error)
        return Resource.Success()
    }

    override suspend fun updateClients(clients: List<ClientEntity>): Resource<Int> {
        val result = clientDao.updateClients(clients)
        if (result == 0) return Resource.Error(resId = R.string.update_client_error)
        return Resource.Success()
    }

    override suspend fun deleteClient(id: Int, action: PendingRemoteAction): Resource<Int> {
        if (action == PendingRemoteAction.COMPLETED) {
            val result = clientDao.delete(id)
            if (result == 0) return Resource.Error(resId = R.string.delete_client_error)
            return Resource.Success()
        }
        val result = clientDao.toPendingDelete(id)
        if (result == 0) return Resource.Error(resId = R.string.delete_client_error)
        return Resource.Success()
    }

    override suspend fun deleteAllClients(): Resource<Int> {
        val result = clientDao.deleteAll()
        if (result == 0) return Resource.Error(resId = R.string.delete_clients_error)
        return Resource.Success()
    }

    private suspend fun areTheSame(client: ClientEntity): Boolean {
        try {
            val clientsList = getClients().first()
            clientsList.data?.forEach {
                val hash1 = listOf(
                    it.remoteId,
                    it.name,
                    it.tradeName,
                    it.paternalName,
                    it.maternalName,
                    it.rfc,
                    it.street,
                    it.noExterior,
                    it.colonia,
                    it.postalCode,
                    it.city,
                    it.state,
                    it.country,
                    it.creditLimit,
                    DateFormat.format(
                        it.dateTimestamp ?: System.currentTimeMillis(),
                        "yyyy-MM-dd"
                    ),
                    it.location
                ).hashCode()
                val hash2 = listOf(
                    client.remoteId,
                    client.name,
                    client.tradeName,
                    client.paternalName,
                    client.maternalName,
                    client.rfc,
                    client.street,
                    client.noExterior,
                    client.colonia,
                    client.postalCode,
                    client.city,
                    client.state,
                    client.country,
                    client.creditLimit,
                    DateFormat.format(
                        client.dateTimestamp ?: System.currentTimeMillis(),
                        "yyyy-MM-dd"
                    ),
                    client.location
                ).hashCode()

                if (hash1 == hash2) return true
            }
        } catch (e: Exception) {
            return false
        }

        return false
    }

    private suspend fun alreadyExists(remoteId: Int): Boolean {
        try {
            val clientsList = getClients().first()
            clientsList.data?.forEach {
                if (remoteId == it.remoteId) return true
            }
        } catch (e: Exception) {
            return false
        }

        return false
    }

    private suspend fun preparingNewClientsToUpdate(
        newClients: List<ClientEntity>
    ): List<ClientEntity> {

        val actualClients = getClients().first()
        newClients.forEach { newClient ->
            actualClients.data?.forEach { actualClient ->
                if (newClient.remoteId == actualClient.remoteId)
                    newClient.id = actualClient.id
            }
        }
        return newClients
    }


}