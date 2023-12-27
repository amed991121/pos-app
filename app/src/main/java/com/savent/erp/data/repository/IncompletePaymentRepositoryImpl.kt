package com.savent.erp.data.repository

import com.savent.erp.data.local.datasource.IncompletePaymentsLocalDataSource
import com.savent.erp.data.local.model.IncompletePaymentEntity
import com.savent.erp.data.remote.datasource.IncompletePaymentsRemoteDatasource
import com.savent.erp.data.remote.model.IncompletePayment
import com.savent.erp.domain.repository.IncompletePaymentRepository
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import com.savent.erp.utils.toLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat

class IncompletePaymentRepositoryImpl(
    private val localDataSource: IncompletePaymentsLocalDataSource,
    private val remoteDatasource: IncompletePaymentsRemoteDatasource
) : IncompletePaymentRepository {

    override suspend fun insertIncompletePayments(incompletePayments: List<IncompletePayment>):
            Resource<Int> =
        localDataSource.insertIncompletePayments(incompletePayments.map { mapToEntity(it) })

    override fun getIncompletePayments(clientId: Int):
            Flow<Resource<List<IncompletePaymentEntity>>> = flow {
        localDataSource.getIncompletePayments(clientId).onEach { emit(it) }.collect()
    }

    override fun getIncompletePayments(): Flow<Resource<List<IncompletePaymentEntity>>> = flow {
        localDataSource.getIncompletePayments().onEach { emit(it) }.collect()
    }

    override suspend fun getIncompletePayment(saleId: Int): Resource<IncompletePaymentEntity> =
        localDataSource.getIncompletePayment(saleId)


    override suspend fun updateIncompletePayment(incompletePayment: IncompletePaymentEntity):
            Resource<Int> =
        localDataSource.updateIncompletePayment(incompletePayment)

    override suspend fun fetchIncompletePayments(
        businessId: Int,
        storeId: Int,
        companyId: Int
    ): Resource<Int> {
        val response = remoteDatasource.getIncompletePayments(businessId, storeId, companyId)
        if (response is Resource.Success) {
            response.data?.let {
                insertIncompletePayments(it)
            }
            return Resource.Success()
        }
        return Resource.Error(resId = response.resId, message = response.message)
    }

    private fun mapToEntity(
        incompletePayment: IncompletePayment
    ): IncompletePaymentEntity {
        return IncompletePaymentEntity(
            0,
            incompletePayment.saleId,
            incompletePayment.clientId,
            incompletePayment.dateTimestamp.toLong(),
            incompletePayment.productsUnits,
            incompletePayment.subtotal,
            incompletePayment.discounts,
            incompletePayment.IVA,
            incompletePayment.collected,
            incompletePayment.total,
            PendingRemoteAction.COMPLETED
        )
    }
}