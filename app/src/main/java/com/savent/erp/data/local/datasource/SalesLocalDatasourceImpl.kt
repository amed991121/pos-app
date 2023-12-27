package com.savent.erp.data.local.datasource

import com.savent.erp.R
import com.savent.erp.data.local.database.dao.SaleDao
import com.savent.erp.data.local.model.SaleEntity
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class SalesLocalDatasourceImpl(private val saleDao: SaleDao) : SalesLocalDatasource {

    override suspend fun insertSale(sale: SaleEntity): Resource<Int> {
        val result = if (!areTheSame(sale)) saleDao.insertSale(sale) else 0L
        if (result == 0L) return Resource.Error(resId = R.string.insert_sale_error)
        return Resource.Success()
    }

    override suspend fun insertSales(sales: List<SaleEntity>): Resource<Int> {
        val newSales = sales.filter { !areTheSame(it) }
        val toInsert = newSales.filter { !alreadyExists(it.remoteId) }
        val toUpdate = preparingNewSalesToUpdate(newSales.minus(toInsert))
        val result1 = saleDao.insertSales(toInsert)
        val result2 = saleDao.updateSales(toUpdate)
        if (result1.size != toInsert.size && result2 != toUpdate.size)
            return Resource.Error(resId = R.string.insert_sales_error)
        return Resource.Success()
    }

    override suspend fun getSale(id: Int): Resource<SaleEntity> =
        Resource.Success(saleDao.getSale(id))

    override suspend fun getSaleByRemoteId(remoteId: Int): Resource<SaleEntity> =
        Resource.Success(saleDao.getSaleByRemoteId(remoteId))

    override fun getSales(): Flow<Resource<List<SaleEntity>>> = flow {
        saleDao.getSales().onEach {
            it?.let { it1 -> emit(Resource.Success(it1)) }
                ?: emit(Resource.Error<List<SaleEntity>>(resId = R.string.get_sales_error))
        }.collect()
    }


    override suspend fun updateSale(sale: SaleEntity): Resource<Int> {
        val result = saleDao.updateSale(sale)
        if (result == 0) return Resource.Error(resId = R.string.update_sale_error)
        return Resource.Success()
    }

    private suspend fun areTheSame(sale: SaleEntity): Boolean {
        try {
            val salesList = getSales().first()
            salesList.data?.forEach {
                val hash1 = listOf(
                    it.remoteId, it.clientId, it.total, it.collected
                ).hashCode()
                val hash2 = listOf(
                    sale.remoteId, sale.clientId, sale.total, sale.collected
                ).hashCode()
                if (hash1 == hash2) return true
            }
        } catch (e: Exception) {
            return false
        }

        return false
    }

    private suspend fun alreadyExists(remoteId: Int): Boolean {
        try {
            val salesList = getSales().first()
            salesList.data?.forEach {
                if (remoteId == it.remoteId) return true
            }
        } catch (e: Exception) {
            return false
        }

        return false
    }

    private suspend fun preparingNewSalesToUpdate(
        newSales: List<SaleEntity>
    ): List<SaleEntity> {

        val actualSales = getSales().first()
        newSales.forEach { newSale ->
            actualSales.data?.forEach { actualSale ->
                if (newSale.remoteId == actualSale.remoteId)
                    newSale.id = actualSale.id
            }
        }
        return newSales
    }

}