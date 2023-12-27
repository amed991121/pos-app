package com.savent.erp.domain.usecase

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.domain.repository.ClientsRepository
import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddClientToSaleUseCase(
    private val salesRepository: SalesRepository,
    private val clientsRepository: ClientsRepository
) {

    suspend operator fun invoke(clientId: Int): Resource<Int> {
        val clientName = clientsRepository.getClient(clientId).data?.name?:""
        return salesRepository.addClientToPendingSale(clientId, clientName)
    }


}