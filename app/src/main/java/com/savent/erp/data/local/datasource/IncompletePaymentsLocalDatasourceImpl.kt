package com.savent.erp.data.local.datasource

import com.savent.erp.R
import com.savent.erp.data.local.database.dao.IncompletePaymentDao
import com.savent.erp.data.local.model.IncompletePaymentEntity
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class IncompletePaymentsLocalDatasourceImpl(private val incompletePaymentDao: IncompletePaymentDao) :
    IncompletePaymentsLocalDataSource {

    override suspend fun insertIncompletePayments(incompletePayments: List<IncompletePaymentEntity>):
            Resource<Int> {
        val newIncompletePayments = incompletePayments.filter { !areTheSame(it) }
        val toInsert = newIncompletePayments.filter { !alreadyExists(it.saleId) }
        val toUpdate = preparingNewIncompletePaymentsToUpdate(newIncompletePayments.minus(toInsert))
        val result1 = incompletePaymentDao.insertIncompletePayments(toInsert)
        val result2 = incompletePaymentDao.updateIncompletePayments(toUpdate)
        if (result1.size != toInsert.size && result2 != toUpdate.size)
            return Resource.Error(resId = R.string.insert_incomplete_payments_error)
        return Resource.Success()
    }

    override fun getIncompletePayments(): Flow<Resource<List<IncompletePaymentEntity>>> = flow {
        incompletePaymentDao.getIncompletePayments().onEach {
            it?.let { it1 -> emit(Resource.Success(it1)) }
                ?: emit(
                    Resource.Error<List<IncompletePaymentEntity>>
                        (resId = R.string.get_incomplete_payments_error)
                )
        }.collect()
    }

    override fun getIncompletePayments(clientId: Int):
            Flow<Resource<List<IncompletePaymentEntity>>> = flow {
        incompletePaymentDao.getIncompletePayments(clientId).onEach {
            it?.let { it1 -> emit(Resource.Success(it1)) }
                ?: emit(
                    Resource.Error<List<IncompletePaymentEntity>>
                        (resId = R.string.get_incomplete_payments_error)
                )
        }.collect()
    }

    override suspend fun getIncompletePayment(saleId: Int): Resource<IncompletePaymentEntity> {
        incompletePaymentDao.getIncompletePayment(saleId)?.let { return Resource.Success(it) }
        return Resource.Error(resId = R.string.get_incomplete_payments_error)
    }


    override suspend fun updateIncompletePayment(incompletePayment: IncompletePaymentEntity):
            Resource<Int> {
        val result = incompletePaymentDao.updateIncompletePayment(incompletePayment)
        if (result == 0) return Resource.Error(resId = R.string.update_incomplete_payments_error)
        return Resource.Success()
    }

    override suspend fun updateIncompletePayments(incompletePayments: List<IncompletePaymentEntity>):
            Resource<Int> {
        val result = incompletePaymentDao.updateIncompletePayments(incompletePayments)
        if (result == 0) return Resource.Error(resId = R.string.update_incomplete_payments_error)
        return Resource.Success()
    }

    override suspend fun deleteProduct(id: Int): Resource<Int> {
        val result = incompletePaymentDao.delete(id)
        if (result == 0) return Resource.Error(resId = R.string.delete_incomplete_payments_error)
        return Resource.Success()
    }

    private suspend fun areTheSame(incompletePayment: IncompletePaymentEntity): Boolean {
        try {
            val incompletePayments = getIncompletePayments().first()
            incompletePayments.data?.forEach {
                val hash1 = listOf(
                    it.saleId, it.clientId, it.productsUnits, it.subtotal,
                    it.discounts, it.IVA, it.total, it.collected
                ).hashCode()
                val hash2 = listOf(
                    incompletePayment.saleId, incompletePayment.clientId,
                    incompletePayment.productsUnits, incompletePayment.subtotal,
                    incompletePayment.discounts, incompletePayment.IVA,
                    incompletePayment.total, incompletePayment.collected
                ).hashCode()
                if (hash1 == hash2) return true
            }
        } catch (e: Exception) {
            return false
        }

        return false
    }

    private suspend fun alreadyExists(saleId: Int): Boolean {
        try {
            val incompletePayments = getIncompletePayments().first()
            incompletePayments.data?.forEach {
                if (saleId == it.saleId) return true
            }
        } catch (e: Exception) {
            return false
        }

        return false
    }

    private suspend fun preparingNewIncompletePaymentsToUpdate(
        newIncompletePayments: List<IncompletePaymentEntity>
    ): List<IncompletePaymentEntity> {

        val actualIncompletePayments = getIncompletePayments().first()
        newIncompletePayments.forEach { newIncompletePayment ->
            actualIncompletePayments.data?.forEach { actualIncompletePayment ->
                if (newIncompletePayment.saleId == actualIncompletePayment.saleId)
                    newIncompletePayment.id = actualIncompletePayment.id
            }
        }
        return newIncompletePayments
    }
}