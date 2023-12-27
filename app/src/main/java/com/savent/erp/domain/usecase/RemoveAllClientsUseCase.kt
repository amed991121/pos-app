package com.savent.erp.domain.usecase

import com.savent.erp.data.local.datasource.ClientsLocalDatasource
import com.savent.erp.utils.Resource

class RemoveAllClientsUseCase(private val clientsLocalDatasource: ClientsLocalDatasource) {

    suspend operator fun invoke(): Resource<Int> =
        clientsLocalDatasource.deleteAllClients()
}