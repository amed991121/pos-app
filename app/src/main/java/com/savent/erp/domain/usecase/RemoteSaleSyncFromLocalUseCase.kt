package com.savent.erp.domain.usecase

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.local.datasource.SalesLocalDatasource
import com.savent.erp.data.local.model.SaleEntity
import com.savent.erp.data.remote.datasource.SalesRemoteDatasource
import com.savent.erp.data.remote.model.Sale
import com.savent.erp.domain.repository.ClientsRepository
import com.savent.erp.domain.repository.ProductsRepository
import com.savent.erp.utils.DateFormat
import com.savent.erp.utils.DateTimeObj
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.java.KoinJavaComponent.inject

class RemoteSaleSyncFromLocalUseCase{

    companion object {

        private val localDatasource: SalesLocalDatasource by inject(
            SalesLocalDatasource::class.java
        )
        private val remoteDatasource: SalesRemoteDatasource by inject(
            SalesRemoteDatasource::class.java
        )
        private val clientsRepository: ClientsRepository by inject(
            ClientsRepository::class.java
        )
        private val pendingTransactions: GetSalesPendingToSendUseCase by inject(
            GetSalesPendingToSendUseCase::class.java
        )

        fun execute(
            businessId: Int,
            sellerId: Int,
            storeId: Int,
            companyId: Int
        ): Resource<Int> =
            synchronized(this){
                runBlocking (Dispatchers.IO){
                    pendingTransactions().let {
                        if (it is Resource.Success) {
                            if (it.data?.isEmpty() == true) Resource.Success(0)
                            it.data?.let { list ->
                                list.forEach { saleEntity ->
                                    executeTransaction(
                                        businessId,
                                        sellerId,
                                        storeId,
                                        saleEntity,
                                        companyId
                                    )
                                }
                            }
                        }
                    }
                    pendingTransactions().let {
                        if (it is Resource.Error || it.data?.isNotEmpty() == true)
                            Resource.Error<Int>(resId = R.string.sync_output_error)
                        Resource.Success(0)
                    }
                }

            }



        private suspend fun executeTransaction(
            businessId: Int, sellerId: Int, storeId: Int,
            saleEntity: SaleEntity, companyId: Int
        ) {
            when (saleEntity.pendingRemoteAction) {
                PendingRemoteAction.INSERT -> {
                    val response = remoteDatasource.insertSale(
                        businessId,
                        sellerId,
                        storeId,
                        mapToApiModel(saleEntity),
                        companyId
                    )
                    if (response is Resource.Success) {
                        saleEntity.remoteId = response.data!!
                        saleEntity.pendingRemoteAction = PendingRemoteAction.COMPLETED
                        localDatasource.updateSale(saleEntity)
                    }
                }

                else -> {
                }
            }
        }

        private suspend fun mapToApiModel(sale: SaleEntity): Sale {
            val client = clientsRepository.getClientByRemoteId(sale.clientId)
            if (client is Resource.Error) return Sale()

            var clientName = ""
            client.data?.let { clientEntity ->
                clientName =
                    (clientEntity.paternalName?:"") + " " + (clientEntity.maternalName?:"") + " " + (clientEntity.name?:"")
            } ?: Resource.Error<Int>(resId = R.string.unknown_error)

            return Sale(
                sale.remoteId,
                sale.clientId,
                clientName,
                DateTimeObj.fromLong(sale.dateTimestamp),
                sale.selectedProducts,
                sale.subtotal,
                sale.discounts,
                sale.IVA,
                sale.IEPS,
                sale.extraDiscountPercent,
                sale.collected,
                sale.total,
                sale.paymentMethod
            )
        }
    }


}