package com.savent.erp.data.repository

import android.util.Log
import com.savent.erp.data.local.datasource.ProductsLocalDatasource
import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.data.remote.datasource.ProductsRemoteDatasource
import com.savent.erp.data.remote.model.Product
import com.savent.erp.domain.repository.ProductsRepository
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class ProductsRepositoryImpl(
    private val localDatasource: ProductsLocalDatasource,
    private val remoteDatasource: ProductsRemoteDatasource
) : ProductsRepository {

    override suspend fun insertProducts(products: List<Product>): Resource<Int> =
        localDatasource.insertProducts(products.map {
            mapToEntity(
                it,
                PendingRemoteAction.COMPLETED
            )
        })

    override suspend fun addProduct(product: Product): Resource<Int> =
        localDatasource.addProduct(mapToEntity(product, PendingRemoteAction.INSERT))

    override suspend fun getProduct(id: Int): Resource<ProductEntity> =
        localDatasource.getProduct(id)

    override suspend fun getProductByRemoteId(remoteId: Long): Resource<ProductEntity> =
        localDatasource.getProduct(remoteId)


    override fun getProducts(): Flow<Resource<List<ProductEntity>>> = flow {
        localDatasource.getProducts().onEach { emit(it) }.collect()
    }

    override fun getProducts(query: String): Flow<Resource<List<ProductEntity>>> = flow {
        localDatasource.getProducts(query).onEach { emit(it) }.collect()
    }

    override suspend fun fetchProducts(
        storeId: Int,
        companyId: Int,
        filter: String
    ):
            Resource<Int> {
        val response = remoteDatasource.getProducts(storeId,companyId, filter)
        if (response is Resource.Success) {
            response.data?.let {
                insertProducts(it)
            }
            return Resource.Success()
        }
        return Resource.Error(resId = response.resId, message = response.message)
    }

    override suspend fun updateProduct(product: Product): Resource<Int> =
        localDatasource.updateProduct(mapToEntity(product, PendingRemoteAction.UPDATE))

    override suspend fun updateProducts(products: List<ProductEntity>): Resource<Int> =
        localDatasource.updateProducts(products)


    override suspend fun deleteProduct(id: Int): Resource<Int> =
        localDatasource.deleteProduct(id, PendingRemoteAction.DELETE)


    private fun mapToEntity(product: Product, actionPending: PendingRemoteAction): ProductEntity {
        return ProductEntity(
            0,
            product.id,
            product.name,
            product.barcode,
            product.description?: "",
            product.image,
            product.price,
            product.IEPS?: 0F,
            product.IVA?: 0F,
            product.units?:0,
            System.currentTimeMillis(),
            actionPending
        )
    }


}