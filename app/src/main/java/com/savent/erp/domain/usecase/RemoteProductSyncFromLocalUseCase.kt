package com.savent.erp.domain.usecase

import com.savent.erp.data.local.datasource.ProductsLocalDatasource
import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.data.remote.datasource.ProductsRemoteDatasource
import com.savent.erp.data.remote.model.Product
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class RemoteProductSyncFromLocalUseCase(
    private val localDatasource: ProductsLocalDatasource,
    private val remoteDatasource: ProductsRemoteDatasource
) {

    suspend operator fun invoke(businessId: Int) {
        localDatasource.getProducts().onEach {
            var pendingTransactions: List<ProductEntity>? = null
            if (it is Resource.Success)
                pendingTransactions = it.data?.filter { productEntity ->
                    productEntity.pendingRemoteAction != PendingRemoteAction.COMPLETED
                }
            pendingTransactions?.let { list->
                list.forEach { productEntity -> executeTransaction(businessId,productEntity) }
            }


        }.collect()
    }

    private suspend fun executeTransaction(businessId: Int, productEntity: ProductEntity) {
        when (productEntity.pendingRemoteAction) {
            PendingRemoteAction.INSERT -> {
                val response = remoteDatasource.insertProduct(
                    businessId,
                    mapToApiModel(productEntity)
                )
                if (response is Resource.Success) {
                    productEntity.remoteId = response.data!!
                    productEntity.pendingRemoteAction = PendingRemoteAction.COMPLETED
                    localDatasource.updateProduct(productEntity)
                }
            }
            PendingRemoteAction.UPDATE -> {
                val response = remoteDatasource.updateProduct(
                    businessId,
                    mapToApiModel(productEntity)
                )
                if (response is Resource.Success) {
                    productEntity.pendingRemoteAction = PendingRemoteAction.COMPLETED
                    localDatasource.updateProduct(productEntity)
                }
            }
            PendingRemoteAction.DELETE -> {
                val response = remoteDatasource.deleteProduct(
                    businessId,
                    productEntity.remoteId
                )
                if (response is Resource.Success) {
                    localDatasource.deleteProduct(productEntity.id,PendingRemoteAction.COMPLETED)
                }
            }
            else -> {}
        }
    }

    private fun mapToApiModel(productEntity: ProductEntity): Product {
        return Product(
            productEntity.remoteId,
            productEntity.name,
            productEntity.barcode,
            productEntity.description,
            productEntity.image,
            productEntity.price,
            productEntity.IEPS,
            productEntity.IVA,
            productEntity.units
        )
    }
}