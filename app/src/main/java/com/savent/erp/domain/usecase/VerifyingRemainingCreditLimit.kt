package com.savent.erp.domain.usecase

import com.savent.erp.R
import com.savent.erp.domain.repository.ClientsRepository
import com.savent.erp.utils.DecimalFormat
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.first

class VerifyingRemainingCreditLimit(
    private val pendingSaleUseCase: GetPendingSaleUseCase,
    private val clientsRepository: ClientsRepository,
    private val getClientListWithDebts: GetClientListWithDebts,
) {
    suspend operator fun invoke(): Resource<Boolean> {

        pendingSaleUseCase().first().data?.let { sale ->
            val clientLocalId = sale.clientId
            var newCreditAmount = 0F
            (sale.total - sale.collected).let {
                if (it <= 0)  return Resource.Success()
                else newCreditAmount = it
            }

            val client = clientsRepository.getClient(clientLocalId)
            val clientsWithDebts = getClientListWithDebts("").first()

            if (client is Resource.Error || client.data == null || clientsWithDebts
                        is Resource.Error || clientsWithDebts.data == null)
                return Resource.Error(resId = R.string.unknown_error)

            val clientRemoteId = client.data.remoteId
            val debts =
                clientsWithDebts.data.find { it.remoteId == clientRemoteId }?.debt ?: 0F
            val creditLimit = client.data.creditLimit?:0F

            (creditLimit - debts).let {
                if (it < 0) return Resource.Error(resId = R.string.credit_limit_exceed)
                if(newCreditAmount > it)
                    return Resource.Error(
                        message = "Cliente con crédito insuficiente.\nCrédito restante de $" +
                                DecimalFormat.format(it)
                    )
                return Resource.Success()
            }
        } ?: return Resource.Error(resId = R.string.unknown_error)

    }
}