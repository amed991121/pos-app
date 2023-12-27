package com.savent.erp.domain.usecase


import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.local.model.ClientEntity
import com.savent.erp.data.local.model.SaleEntity
import com.savent.erp.domain.repository.ClientsRepository
import com.savent.erp.presentation.ui.model.ClientItem
import com.savent.erp.utils.DateFormat
import com.savent.erp.utils.NameFormat
import com.savent.erp.utils.PendingRemoteAction

import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class GetClientListUseCase(
    private val clientsRepository: ClientsRepository,
    private val getPendingSaleUseCase: GetPendingSaleUseCase
) {

    operator fun invoke(query: String): Flow<Resource<List<ClientItem>>> = flow {
        getClientsFlow(query).combine(getPendingSaleUseCase()) { clients, pendingSale ->
            if (clients is Resource.Success && clients.data != null)
                emit(Resource.Success(clients.data.map { mapToUiItem(it, pendingSale.data) }))
            else
                emit(Resource.Error<List<ClientItem>>(resId = R.string.get_clients_error))
        }.collect()


    }

    private fun getClientsFlow(query: String): Flow<Resource<List<ClientEntity>>> = flow {
        clientsRepository.getClients(query).onEach {
            if (it is Resource.Success)
                it.data?.let { it1 ->
                    emit(Resource.Success(it1.filter { clientEntity ->
                        clientEntity.pendingRemoteAction != PendingRemoteAction.DELETE
                    }))
                } ?: emit(Resource.Error<List<ClientEntity>>(resId = R.string.get_clients_error))
            else
                emit(Resource.Error<List<ClientEntity>>(resId = R.string.get_clients_error))
        }.collect()
    }


    private fun mapToUiItem(client: ClientEntity, pendingSale: SaleEntity?): ClientItem {
        var name = NameFormat.format(
            client.name + " ${client.paternalName} " +
                    "${client.maternalName}"
        )
        client.tradeName?.let { if (it.isNotEmpty()) name = "${NameFormat.format(it)} ($name)" }
        return ClientItem(
            client.id,
            name,
            client.image,
            NameFormat.format(client.city + ", " + client.country),
            pendingSale?.clientId == client.id
        )
    }
}