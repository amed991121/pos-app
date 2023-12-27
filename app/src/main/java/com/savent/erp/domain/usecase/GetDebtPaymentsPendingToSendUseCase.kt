package com.savent.erp.domain.usecase

import com.savent.erp.data.local.datasource.DebtPaymentLocalDatasource
import com.savent.erp.data.local.model.DebtPaymentEntity
import com.savent.erp.utils.IsFromToday
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.first

class GetDebtPaymentsPendingToSendUseCase(private val localDatasource: DebtPaymentLocalDatasource) {

    suspend operator fun invoke(): Resource<List<DebtPaymentEntity>> {
        val debts = localDatasource.getDebtPayments().first()
        return if (debts is Resource.Success) {
            val pendingTransactions: List<DebtPaymentEntity> = debts.data?.filter { debtEntity ->
                debtEntity.pendingRemoteAction != PendingRemoteAction.COMPLETED && IsFromToday(
                    debtEntity.dateTimestamp
                )
            }?: listOf()
            return Resource.Success(pendingTransactions)
        } else debts
    }
}