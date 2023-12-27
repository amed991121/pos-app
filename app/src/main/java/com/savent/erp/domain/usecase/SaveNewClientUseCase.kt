package com.savent.erp.domain.usecase

import com.savent.erp.data.remote.model.Client
import com.savent.erp.domain.repository.ClientsRepository
import com.savent.erp.utils.Resource

class SaveNewClientUseCase(
    private val clientsRepository: ClientsRepository
) {
    suspend operator fun invoke(client: Client):Resource<Int> =
        clientsRepository.addClient(client)

}