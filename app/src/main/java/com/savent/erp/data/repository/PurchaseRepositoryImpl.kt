package com.savent.erp.data.repository

import com.savent.erp.data.common.model.Purchase
import com.savent.erp.data.local.datasource.PurchaseLocalDatasource
import com.savent.erp.data.remote.datasource.PurchaseRemoteDatasource
import com.savent.erp.domain.repository.PurchasesRepository
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class PurchaseRepositoryImpl(
    private val localDatasource: PurchaseLocalDatasource,
    private val remoteDatasource: PurchaseRemoteDatasource
) : PurchasesRepository {

    override suspend fun insertPurchases(purchases: List<Purchase>): Resource<Int> =
        localDatasource.insertPurchases(purchases)

    override suspend fun getPurchase(id: Int): Resource<Purchase> =
        localDatasource.getPurchase(id)

    override suspend fun updatePurchase(purchase: Purchase): Resource<Int> =
        localDatasource.updatePurchase(purchase)

    override fun getPurchases(): Flow<Resource<List<Purchase>>> = flow {
        localDatasource.getPurchases().onEach { emit(it) }.collect()
    }

    override suspend fun fetchPurchases(companyId: Int): Resource<Int> {
        val response = remoteDatasource.getPurchases(companyId)
        if (response is Resource.Error || response.data == null) return Resource.Error(
            resId = response.resId,
            message = response.message
        )
        return localDatasource.insertPurchases(response.data)
    }
}