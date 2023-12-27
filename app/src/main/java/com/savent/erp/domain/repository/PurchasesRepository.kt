package com.savent.erp.domain.repository

import com.savent.erp.data.common.model.Purchase
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PurchasesRepository {

    suspend fun insertPurchases(purchases: List<Purchase>): Resource<Int>

    suspend fun getPurchase(id: Int): Resource<Purchase>

    suspend fun updatePurchase(purchase: Purchase): Resource<Int>

    fun getPurchases(): Flow<Resource<List<Purchase>>>

    suspend fun fetchPurchases(companyId: Int): Resource<Int>

}