package com.savent.erp.data.local.datasource

import com.savent.erp.data.local.model.IncompletePaymentEntity
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IncompletePaymentsLocalDataSource {

    suspend fun insertIncompletePayments(incompletePayments: List<IncompletePaymentEntity>):
            Resource<Int>

    fun getIncompletePayments(): Flow<Resource<List<IncompletePaymentEntity>>>

    fun getIncompletePayments(clientId: Int): Flow<Resource<List<IncompletePaymentEntity>>>

    suspend fun getIncompletePayment(saleId: Int): Resource<IncompletePaymentEntity>

    suspend fun updateIncompletePayment(incompletePayment: IncompletePaymentEntity):
            Resource<Int>

    suspend fun updateIncompletePayments(incompletePayments: List<IncompletePaymentEntity>):
            Resource<Int>

    suspend fun deleteProduct(id: Int): Resource<Int>
}