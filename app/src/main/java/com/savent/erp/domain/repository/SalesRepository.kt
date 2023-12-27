package com.savent.erp.domain.repository

import com.savent.erp.data.local.model.SaleEntity
import com.savent.erp.data.remote.model.Client
import com.savent.erp.data.remote.model.Sale
import com.savent.erp.utils.PaymentMethod
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SalesRepository {

    suspend fun insertSale(sale: SaleEntity): Resource<Int>

    suspend fun insertSales(sales: List<Sale>): Resource<Int>

    suspend fun getSale(id: Int): Resource<SaleEntity>

    suspend fun getSaleByRemoteId(remoteId: Int): Resource<SaleEntity>

    fun getSales(): Flow<Resource<List<SaleEntity>>>

    suspend fun fetchSales(
        businessId: Int,
        storeId: Int,
        date: String,
        companyId: Int
    ): Resource<Int>

    suspend fun createPendingSale(): Resource<Int>

    suspend fun addClientToPendingSale(clientId: Int, clientName: String): Resource<Int>

    suspend fun addProductToPendingSale(productId: Int): Resource<Int>

    suspend fun removeProductFromPendingSale(productId: Int): Resource<Int>

    suspend fun changeProductUnits(productId: Int, units: Int): Resource<Int>

    suspend fun increaseExtraDiscountPercent(): Resource<Int>

    suspend fun decreaseExtraDiscountPercent(): Resource<Int>

    suspend fun updateExtraDiscountPercent(discount: Int): Resource<Int>

    suspend fun updatePaymentMethod(method: PaymentMethod): Resource<Int>

    suspend fun updatePaymentCollected(collected: Float): Resource<Int>

    fun getPendingSale(): Flow<Resource<SaleEntity>>

    suspend fun updatePendingSale(sale: SaleEntity): Resource<Int>

    suspend fun deletePendingSale(): Resource<Int>

}