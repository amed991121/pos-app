package com.savent.erp.data.local.datasource

import android.util.Log
import com.savent.erp.R
import com.savent.erp.data.common.model.Discount
import com.savent.erp.data.local.database.dao.DiscountsDao
import com.savent.erp.utils.Resource

class DiscountsLocalDatasourceImpl(private val discountsDao: DiscountsDao) :
    DiscountsLocalDatasource {

    override suspend fun insertDiscounts(discounts: List<Discount>): Resource<Int> {
        val result = discountsDao.insertDiscounts(discounts)
        if (result.size != discounts.size)
            return Resource.Error(resId = R.string.insert_discounts_error)
        return Resource.Success()
    }

    override suspend fun getDiscount(productId: Int, clientId: Int): Resource<Discount> {
        return try {
            Resource.Success(discountsDao.getDiscount(productId, clientId))
        } catch (e: Exception) {
            Resource.Error(resId = R.string.get_discounts_error)
        }
    }

    override suspend fun deleteDiscounts(clientId: Int): Resource<Int> =
       Resource.Success(discountsDao.deleteDiscount(clientId))
}