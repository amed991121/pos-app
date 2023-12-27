package com.savent.erp.domain.repository

import com.savent.erp.data.local.model.DebtPaymentEntity
import com.savent.erp.data.remote.model.DebtPayment
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface DebtPaymentRepository {

    suspend fun insertDebtPayments(debtPayments: List<DebtPayment>): Resource<Int>

    suspend fun insertDebtPayment(debtPayment: DebtPayment): Resource<Int>

    fun getDebtPayments(): Flow<Resource<List<DebtPaymentEntity>>>

    suspend fun fetchDebtPayments(storeId: Int, companyId: Int): Resource<Int>
}