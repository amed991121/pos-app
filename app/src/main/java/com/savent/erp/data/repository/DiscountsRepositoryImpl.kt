package com.savent.erp.data.repository

import com.savent.erp.data.common.model.Discount
import com.savent.erp.data.local.datasource.DiscountsLocalDatasource
import com.savent.erp.data.remote.datasource.DiscountsRemoteDatasource
import com.savent.erp.domain.repository.DiscountsRepository
import com.savent.erp.utils.Resource

class DiscountsRepositoryImpl(
    private val localDatasource: DiscountsLocalDatasource,
    private val remoteDatasource: DiscountsRemoteDatasource
) : DiscountsRepository {

    override suspend fun insertDiscounts(discounts: List<Discount>): Resource<Int> =
        localDatasource.insertDiscounts(discounts)

    override suspend fun getDiscount(productId: Int, clientId: Int): Resource<Discount> =
        localDatasource.getDiscount(productId, clientId)

    override suspend fun fetchDiscounts(
        storeId: Int,
        clientId: Int
    ): Resource<Int> {
        val result = remoteDatasource.getDiscounts(storeId, clientId)
        if (result is Resource.Error || result.data == null) return Resource.Error(result.resId)
        return localDatasource.insertDiscounts(result.data)
    }

    override suspend fun deleteDiscounts(clientId: Int): Resource<Int> =
        localDatasource.deleteDiscounts(clientId)

}