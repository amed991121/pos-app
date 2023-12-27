package com.savent.erp.data.local.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.data.local.model.SaleEntity
import com.savent.erp.utils.PaymentMethod
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class PendingSaleLocalDatasourceImpl(
    private val dataObjectStorage: DataObjectStorage<SaleEntity>
) : PendingSaleLocalDatasource {

    override suspend fun createPendingSale(): Resource<Int> =
        dataObjectStorage.saveData(SaleEntity())


    override fun getPendingSale(): Flow<Resource<SaleEntity>> = flow {
        dataObjectStorage.getData().onEach { emit(it) }.collect()
    }

    override suspend fun updatePendingSale(sale: SaleEntity): Resource<Int> =
        dataObjectStorage.saveData(sale)


    override suspend fun addClientToPendingSale(clientId: Int, clientName: String): Resource<Int> {
        val pendingSale = dataObjectStorage.getData().first().data
        pendingSale?.let {
            it.clientId = clientId
            it.clientName = clientName
            return dataObjectStorage.saveData(it)
        }
        return Resource.Error()
    }

    override suspend fun addProductToPendingSale(productId: Int): Resource<Int> {
        val pendingSale = dataObjectStorage.getData().first().data
        pendingSale?.let {
            val unitsSelected = it.selectedProducts[productId]
            return changeProductUnits(productId, unitsSelected?.inc() ?: 1)
        }
        return Resource.Error()
    }

    override suspend fun removeProductFromPendingSale(productId: Int): Resource<Int> {
        val pendingSale = dataObjectStorage.getData().first().data
        pendingSale?.let {
            val unitsSelected = it.selectedProducts[productId]
            unitsSelected?.let { it1 -> return changeProductUnits(productId, it1.dec()) }
        }
        return Resource.Error()
    }

    override suspend fun changeProductUnits(productId: Int, units: Int): Resource<Int> {
        val pendingSale = dataObjectStorage.getData().first().data
        pendingSale?.let {
            it.selectedProducts[productId] = units
            if (it.selectedProducts[productId] == 0) it.selectedProducts.remove(productId)
            return dataObjectStorage.saveData(it)
        }
        return Resource.Error()
    }

    override suspend fun increaseExtraDiscountPercent(): Resource<Int> {
        val pendingSale = dataObjectStorage.getData().first().data
        pendingSale?.let {
            return updateExtraDiscountPercent(it.extraDiscountPercent.inc())
        }
        return Resource.Error()
    }

    override suspend fun decreaseExtraDiscountPercent(): Resource<Int> {
        val pendingSale = dataObjectStorage.getData().first().data
        pendingSale?.let {
            it.extraDiscountPercent.dec().let { it1->
                return if (it1 > 0)
                    updateExtraDiscountPercent(it1)
                else updateExtraDiscountPercent(0)
            }

        }
        return Resource.Error()
    }

    override suspend fun updateExtraDiscountPercent(discount: Int): Resource<Int> {
        val pendingSale = dataObjectStorage.getData().first().data
        pendingSale?.let {
            it.extraDiscountPercent = discount
            return dataObjectStorage.saveData(it)
        }
        return Resource.Error()
    }

    override suspend fun updatePaymentCollected(collected: Float): Resource<Int> {
        val pendingSale = dataObjectStorage.getData().first().data
        pendingSale?.let {
            it.collected = collected
            return dataObjectStorage.saveData(it)
        }
        return Resource.Error()
    }

    override suspend fun updatePaymentMethod(method: PaymentMethod): Resource<Int> {
        val pendingSale = dataObjectStorage.getData().first().data
        pendingSale?.let {
            it.paymentMethod = method
            return dataObjectStorage.saveData(it)
        }
        return Resource.Error()
    }


    override suspend fun deletePendingSale(): Resource<Int> =
        dataObjectStorage.clear()
}