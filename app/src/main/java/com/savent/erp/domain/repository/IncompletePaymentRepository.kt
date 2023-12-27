package com.savent.erp.domain.repository

import com.savent.erp.data.local.model.IncompletePaymentEntity
import com.savent.erp.data.remote.model.IncompletePayment
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IncompletePaymentRepository {

    suspend fun insertIncompletePayments(incompletePayments: List<IncompletePayment>): Resource<Int>

    fun getIncompletePayments(clientId: Int): Flow<Resource<List<IncompletePaymentEntity>>>

    fun getIncompletePayments(): Flow<Resource<List<IncompletePaymentEntity>>>

    suspend fun getIncompletePayment(saleId: Int): Resource<IncompletePaymentEntity>

    suspend fun updateIncompletePayment(incompletePayment: IncompletePaymentEntity): Resource<Int>

    suspend fun fetchIncompletePayments(businessId: Int, storeId: Int, companyId: Int):
            Resource<Int>
}