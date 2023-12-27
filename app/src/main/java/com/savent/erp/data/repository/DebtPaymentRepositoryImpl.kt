package com.savent.erp.data.repository

import com.savent.erp.data.local.datasource.DebtPaymentLocalDatasource
import com.savent.erp.data.local.model.DebtPaymentEntity
import com.savent.erp.data.remote.datasource.DebtPaymentRemoteDatasource
import com.savent.erp.data.remote.model.DebtPayment
import com.savent.erp.domain.repository.DebtPaymentRepository
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import com.savent.erp.utils.toLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat

class DebtPaymentRepositoryImpl(
    private val localDatasource: DebtPaymentLocalDatasource,
    private val remoteDatasource: DebtPaymentRemoteDatasource
) : DebtPaymentRepository {

    override suspend fun insertDebtPayments(debtPayments: List<DebtPayment>): Resource<Int> =
        localDatasource.insertDebtPayments(debtPayments.map { mapToEntity(it) })

    override suspend fun insertDebtPayment(debtPayment: DebtPayment): Resource<Int> =
        localDatasource.insertDebtPayment(mapToEntity(debtPayment, PendingRemoteAction.INSERT))

    override fun getDebtPayments(): Flow<Resource<List<DebtPaymentEntity>>> = flow {
        localDatasource.getDebtPayments().onEach { emit(it) }.collect()
    }

    override suspend fun fetchDebtPayments(storeId: Int, companyId: Int): Resource<Int> {
        val response = remoteDatasource.getDebtPayments(storeId,companyId)
        if (response is Resource.Error || response.data == null) return Resource.Error(
            resId = response.resId,
            message = response.message
        )
        return localDatasource.insertDebtPayments(response.data.map { mapToEntity(it) })
    }

    private fun mapToEntity(
        debtPayment: DebtPayment,
        pendingRemoteAction: PendingRemoteAction = PendingRemoteAction.COMPLETED
    ): DebtPaymentEntity {
        return DebtPaymentEntity(
            0,
            debtPayment.id,
            debtPayment.saleId,
            debtPayment.clientId,
            debtPayment.saleTimestamp.toLong(),
            debtPayment.dateTimestamp.toLong(),
            debtPayment.toPay,
            debtPayment.paid,
            debtPayment.paymentMethod,
            pendingRemoteAction
        )
    }
}