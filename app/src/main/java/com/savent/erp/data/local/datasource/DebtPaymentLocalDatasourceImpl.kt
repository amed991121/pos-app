package com.savent.erp.data.local.datasource

import com.savent.erp.R
import com.savent.erp.data.local.database.dao.DebtPaymentDao
import com.savent.erp.data.local.model.DebtPaymentEntity
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class DebtPaymentLocalDatasourceImpl(private val debtPaymentDao: DebtPaymentDao):
    DebtPaymentLocalDatasource {

    override suspend fun insertDebtPayments(debtPayments: List<DebtPaymentEntity>): Resource<Int> {
        return if(debtPaymentDao.insertDebtPayments(debtPayments).size == debtPayments.size)
            Resource.Success(0)
        else Resource.Error(R.string.insert_debt_payments_error)
    }

    override suspend fun insertDebtPayment(debtPayment: DebtPaymentEntity): Resource<Int> {
        val result = debtPaymentDao.insertDebtPayment(debtPayment)
        if (result == 0L) return Resource.Error(resId = R.string.insert_debt_payments_error)
        return Resource.Success()
    }

    override fun getDebtPayments(): Flow<Resource<List<DebtPaymentEntity>>> = flow {
        debtPaymentDao.getDebtPayments().onEach {
            it?.let { it1 -> emit(Resource.Success(it1)) }
                ?: emit(Resource.Error<List<DebtPaymentEntity>>(resId = R.string.get_debt_payments_error))
        }.collect()
    }

    override suspend fun updateDebtPayment(debtPayment: DebtPaymentEntity): Resource<Int> {
        val result = debtPaymentDao.updateDebtPayment(debtPayment)
        if (result == 0) return Resource.Error(resId = R.string.update_debt_payments_error)
        return Resource.Success()
    }
}