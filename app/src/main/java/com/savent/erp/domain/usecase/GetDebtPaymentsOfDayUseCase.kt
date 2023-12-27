package com.savent.erp.domain.usecase

import com.savent.erp.R
import com.savent.erp.data.local.model.DebtPaymentEntity
import com.savent.erp.domain.repository.ClientsRepository
import com.savent.erp.domain.repository.DebtPaymentRepository
import com.savent.erp.presentation.ui.model.DebtPaymentItem
import com.savent.erp.utils.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class GetDebtPaymentsOfDayUseCase(
    private val debtPaymentRepository: DebtPaymentRepository,
    private val clientsRepository: ClientsRepository,
) {
    operator fun invoke(clientName: String): Flow<Resource<List<DebtPaymentItem>>> = flow {
        debtPaymentRepository.getDebtPayments().onEach {
            if (it is Resource.Error || it.data == null) {
                emit(
                    Resource.Error(
                        resId = R.string.get_debt_payments_error
                    )
                )
                return@onEach
            }

            val debtPayments: List<DebtPaymentItem> = it.data.filter { entity ->
                IsFromToday(entity.dateTimestamp)
            }.map { it1 ->
                lateinit var clientName: String
                clientsRepository.getClientByRemoteId(it1.clientId).let { client ->
                    if (client is Resource.Error || client.data == null) {
                        emit(
                            Resource.Error(
                                resId = R.string.get_clients_error
                            )
                        )
                        return@onEach
                    }

                    val clientEntity = client.data
                    clientName = NameFormat.format(
                        clientEntity.name + " ${clientEntity.paternalName} " +
                                "${clientEntity.maternalName}"
                    )
                    clientEntity.tradeName?.let { tradeName ->
                        if (tradeName.isNotEmpty()) clientName =
                            "${NameFormat.format(tradeName)} ($clientName)"
                    }

                }

                val saleDateTime = DateFormat.format(it1.saleTimestamp, "yyyy-MM-dd")
                mapToUiItem(it1, clientName, saleDateTime)

            }.filter { it2 -> it2.clientName.contains(clientName, true) }

            emit(Resource.Success(debtPayments))

        }.collect()
    }

    private fun mapToUiItem(
        entity: DebtPaymentEntity,
        clientName: String,
        saleDateTime: String
    ): DebtPaymentItem {
        return DebtPaymentItem(
            entity.remoteId,
            clientName,
            saleDateTime,
            DateFormat.format(entity.dateTimestamp, "hh:mm a"),
            entity.paymentMethod,
            entity.toPay,
            entity.paid,
            entity.pendingRemoteAction != PendingRemoteAction.COMPLETED
        )
    }
}