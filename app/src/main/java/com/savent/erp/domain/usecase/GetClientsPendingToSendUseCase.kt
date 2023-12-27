package com.savent.erp.domain.usecase

import com.savent.erp.data.local.datasource.ClientsLocalDatasource
import com.savent.erp.data.local.model.ClientEntity
import com.savent.erp.utils.DateFormat
import com.savent.erp.utils.IsFromToday
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.first

class GetClientsPendingToSendUseCase(private val localDatasource: ClientsLocalDatasource) {

    suspend operator fun invoke(): Resource<List<ClientEntity>> {
        val clients = localDatasource.getClients().first()
        return if (clients is Resource.Success) {
            val pendingTransactions: List<ClientEntity> = clients.data?.filter { clientEntity ->
                clientEntity.pendingRemoteAction != PendingRemoteAction.COMPLETED && IsFromToday(
                    clientEntity.dateTimestamp ?: System.currentTimeMillis()
                )
            }?: listOf()
            Resource.Success(pendingTransactions)
        } else {
            clients
        }
    }
}