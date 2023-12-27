package com.savent.erp.data.repository

import com.savent.erp.data.local.datasource.PendingSaleLocalDatasource
import com.savent.erp.data.local.datasource.SalesLocalDatasource
import com.savent.erp.data.local.model.SaleEntity
import com.savent.erp.data.remote.datasource.SalesRemoteDatasource
import com.savent.erp.data.remote.model.Sale
import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.utils.PaymentMethod
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import com.savent.erp.utils.toLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat

class SalesRepositoryImpl(
    private val localDatasource: SalesLocalDatasource,
    private val remoteDatasource: SalesRemoteDatasource,
    private val pendingLocalDataSource: PendingSaleLocalDatasource,
) : SalesRepository {

    override suspend fun insertSale(sale: SaleEntity): Resource<Int> =
        localDatasource.insertSale(sale)

    override suspend fun insertSales(sales: List<Sale>): Resource<Int> =
        localDatasource.insertSales(sales.map {
            mapToEntity(
                it,
                PendingRemoteAction.COMPLETED
            )
        })

    override suspend fun getSale(id: Int): Resource<SaleEntity> =
        localDatasource.getSale(id)

    override suspend fun getSaleByRemoteId(remoteId: Int): Resource<SaleEntity> =
        localDatasource.getSaleByRemoteId(remoteId)

    override fun getSales(): Flow<Resource<List<SaleEntity>>> = flow {
        localDatasource.getSales().onEach { emit(it) }.collect()
    }

    override suspend fun fetchSales(
        businessId: Int,
        storeId: Int,
        date: String,
        companyId: Int
    ):
            Resource<Int> {
        val response = remoteDatasource.getSales(businessId, storeId, date, companyId)
        if (response is Resource.Success) {
            response.data?.let {
                insertSales(it)
            }
            return Resource.Success()
        }
        return Resource.Error(resId = response.resId)
    }

    override suspend fun createPendingSale(): Resource<Int> =
        pendingLocalDataSource.createPendingSale()

    override suspend fun addClientToPendingSale(clientId: Int, clientName: String): Resource<Int> =
        pendingLocalDataSource.addClientToPendingSale(clientId, clientName)

    override suspend fun addProductToPendingSale(productId: Int): Resource<Int> =
        pendingLocalDataSource.addProductToPendingSale(productId)

    override suspend fun changeProductUnits(productId: Int, units: Int): Resource<Int> =
        pendingLocalDataSource.changeProductUnits(productId, units)

    override suspend fun removeProductFromPendingSale(productId: Int): Resource<Int> =
        pendingLocalDataSource.removeProductFromPendingSale(productId)

    override suspend fun increaseExtraDiscountPercent(): Resource<Int> =
        pendingLocalDataSource.increaseExtraDiscountPercent()


    override suspend fun decreaseExtraDiscountPercent(): Resource<Int> =
        pendingLocalDataSource.decreaseExtraDiscountPercent()

    override suspend fun updateExtraDiscountPercent(discount: Int): Resource<Int> =
        pendingLocalDataSource.updateExtraDiscountPercent(discount)

    override suspend fun updatePaymentMethod(method: PaymentMethod): Resource<Int> =
        pendingLocalDataSource.updatePaymentMethod(method)


    override suspend fun updatePaymentCollected(collected: Float): Resource<Int> =
        pendingLocalDataSource.updatePaymentCollected(collected)


    override fun getPendingSale(): Flow<Resource<SaleEntity>> = flow {
        pendingLocalDataSource.getPendingSale().onEach { emit(it) }.collect()
    }

    override suspend fun updatePendingSale(sale: SaleEntity): Resource<Int> =
        pendingLocalDataSource.updatePendingSale(sale)


    override suspend fun deletePendingSale(): Resource<Int> =
        pendingLocalDataSource.deletePendingSale()

    private fun mapToEntity(sale: Sale, actionPending: PendingRemoteAction): SaleEntity {
        return SaleEntity(
            0,
            sale.id,
            sale.clientId ?: 0,
            sale.clientName ?: "",
            sale.dateTimestamp?.toLong()
                ?: System.currentTimeMillis(),
            sale.selectedProducts,
            sale.subtotal,
            sale.discounts,
            sale.IVA,
            sale.IEPS,
            sale.extraDiscountPercent,
            sale.collected,
            sale.total,
            sale.paymentMethod,
            actionPending
        )
    }


}