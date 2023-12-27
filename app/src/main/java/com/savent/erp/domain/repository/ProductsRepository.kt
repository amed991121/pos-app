package com.savent.erp.domain.repository

import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.utils.Resource
import com.savent.erp.data.remote.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {

    suspend fun insertProducts(products: List<Product>): Resource<Int>

    suspend fun addProduct(product: Product): Resource<Int>

    suspend fun getProduct(id: Int): Resource<ProductEntity>

    suspend fun getProductByRemoteId(remoteId: Long): Resource<ProductEntity>

    fun getProducts(): Flow<Resource<List<ProductEntity>>>

    fun getProducts(query: String): Flow<Resource<List<ProductEntity>>>

    suspend fun fetchProducts(
        storeId: Int,
        companyId: Int,
        filter: String
    ): Resource<Int>

    suspend fun updateProduct(product: Product): Resource<Int>

    suspend fun updateProducts(products: List<ProductEntity>): Resource<Int>

    suspend fun deleteProduct(id: Int): Resource<Int>
}