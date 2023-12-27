package com.savent.erp.data.local.datasource

import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.utils.Resource
import com.savent.erp.utils.PendingRemoteAction
import kotlinx.coroutines.flow.Flow

interface ProductsLocalDatasource {

    suspend fun insertProducts(products: List<ProductEntity>): Resource<Int>

    suspend fun addProduct(product: ProductEntity): Resource<Int>

    suspend fun getProduct(id: Int): Resource<ProductEntity>

    suspend fun getProduct(remoteId: Long): Resource<ProductEntity>

    fun getProducts(): Flow<Resource<List<ProductEntity>>>

    fun getProducts(query: String): Flow<Resource<List<ProductEntity>>>

    suspend fun updateProduct(product: ProductEntity): Resource<Int>

    suspend fun updateProducts(products: List<ProductEntity>): Resource<Int>

    suspend fun deleteProduct(id: Int, action: PendingRemoteAction): Resource<Int>

    suspend fun deleteAllProducts(): Resource<Int>


}