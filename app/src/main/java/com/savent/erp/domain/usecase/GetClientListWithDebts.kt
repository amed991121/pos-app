package com.savent.erp.domain.usecase

import com.savent.erp.R
import com.savent.erp.data.local.model.IncompletePaymentEntity
import com.savent.erp.domain.repository.ClientsRepository
import com.savent.erp.domain.repository.IncompletePaymentRepository
import com.savent.erp.presentation.ui.model.ClientWithDebtItem
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class GetClientListWithDebts(
    private val incompletePaymentRepository: IncompletePaymentRepository,
    private val getClientListUseCase: GetClientListUseCase,
    private val clientsRepository: ClientsRepository
) {
    suspend operator fun invoke(query: String): Flow<Resource<List<ClientWithDebtItem>>> = flow {
        getClientListUseCase(query).combine(getAllIncompletePayments()) { clients, payments ->
            if (clients is Resource.Success && payments is Resource.Success
                && clients.data != null && payments.data != null
            ) {
                val clientsWithDebts = mutableListOf<ClientWithDebtItem>()

                clients.data.forEach {
                    var debt = 0F
                    val clientRemoteId = clientsRepository.getClient(it.localId).data?.remoteId?:0
                    payments.data.forEach { it1 ->
                        if (it1.clientId == clientRemoteId) {
                            debt += it1.total - it1.collected
                        }
                    }

                    if (debt > 0)
                        clientsWithDebts.add(ClientWithDebtItem(
                            clientRemoteId, it.name, it.image, it.address, debt
                        ))

                }
                clientsWithDebts.sortByDescending { it1-> it1.debt }
                emit(Resource.Success(clientsWithDebts.toList()))
            } else
                emit(Resource.Error<List<ClientWithDebtItem>>(resId = R.string.get_clients_error))
        }.collect()
    }

    suspend fun getAllIncompletePayments(): Flow<Resource<List<IncompletePaymentEntity>>> = flow {
        incompletePaymentRepository.getIncompletePayments().onEach {
            if (it is Resource.Error) {
                emit(
                    Resource.Error(
                        resId = R.string.get_incomplete_payments_error
                    )
                )
                return@onEach
            }

            emit(Resource.Success(it.data?.filter { entity ->
                entity.collected < entity.total
            } ?: listOf()))

        }.collect()
    }


}