package com.savent.erp.data.local.datasource

import android.util.Log
import com.savent.erp.R
import com.savent.erp.data.common.model.Purchase
import com.savent.erp.data.local.database.dao.PurchaseDao
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class PurchaseLocalDatasourceImpl(private val purchaseDao: PurchaseDao) : PurchaseLocalDatasource {

    override suspend fun insertPurchases(purchases: List<Purchase>): Resource<Int> {
        purchaseDao.deleteAll()
        val result = purchaseDao.insertPurchases(purchases)
        if (result.size != purchases.size)
            return Resource.Error(resId = R.string.insert_purchases_error)
        return Resource.Success()
    }

    override suspend fun getPurchase(id: Int): Resource<Purchase> =
        Resource.Success(purchaseDao.getPurchase(id))

    override fun getPurchases(): Flow<Resource<List<Purchase>>> = flow {
        purchaseDao.getPurchases().onEach {
            it?.let { it1 -> emit(Resource.Success(it1)) }
                ?: emit(Resource.Error(resId = R.string.get_purchases_error))
        }.collect()
    }

    override suspend fun updatePurchase(purchase: Purchase): Resource<Int> {
        val result = purchaseDao.updatePurchase(purchase)
        if(result > 0) return Resource.Success(0)
        return Resource.Error(resId = R.string.update_purchase_error)
    }
}