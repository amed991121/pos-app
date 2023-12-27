package com.savent.erp.data.local.datasource

import com.savent.erp.R
import com.savent.erp.data.local.database.dao.ProductDao
import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.utils.DateFormat
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class ProductsLocalDatasourceImpl(private val productDao: ProductDao) : ProductsLocalDatasource {

    override suspend fun insertProducts(products: List<ProductEntity>): Resource<Int> {
        val newProducts = products.filter { !areTheSame(it) }
        val toInsert = newProducts.filter { !alreadyExists(it.remoteId) }
        val toUpdate = preparingNewProductsToUpdate(newProducts.minus(toInsert))
        val result1 = productDao.insertProducts(toInsert)
        val result2 = productDao.updateProducts(toUpdate)
        if (result1.size != toInsert.size && result2 != toUpdate.size)
            return Resource.Error(resId = R.string.insert_products_error)
        return Resource.Success()

    }

    override suspend fun addProduct(product: ProductEntity): Resource<Int> {
        val result = if (!areTheSame(product)) productDao.addProduct(product) else 0L
        if (result == 0L) return Resource.Error(resId = R.string.add_product_error)
        return Resource.Success()
    }

    override suspend fun getProduct(id: Int): Resource<ProductEntity> {
        return try {
            Resource.Success(productDao.getProduct(id))
        } catch (e: Exception) {
            Resource.Error(resId = R.string.get_products_error)
        }
    }

    override suspend fun getProduct(remoteId: Long): Resource<ProductEntity> {
        return try {
            Resource.Success(productDao.getProduct(remoteId))
        } catch (e: Exception) {
            Resource.Error(resId = R.string.get_products_error)
        }
    }


    override fun getProducts(): Flow<Resource<List<ProductEntity>>> = flow {
        productDao.getProducts().onEach {
            it?.let { it1 -> emit(Resource.Success(it1)) }
                ?: emit(Resource.Error<List<ProductEntity>>(resId = R.string.get_products_error))
        }.collect()
    }

    override fun getProducts(query: String): Flow<Resource<List<ProductEntity>>> = flow {
        productDao.getProducts(query).onEach {
            it?.let { it1 -> emit(Resource.Success(it1)) }
                ?: emit(Resource.Error<List<ProductEntity>>(resId = R.string.get_products_error))
        }.collect()
    }

    override suspend fun updateProduct(product: ProductEntity): Resource<Int> {
        val result = productDao.updateProduct(product)
        if (result == 0) return Resource.Error(resId = R.string.update_product_error)
        return Resource.Success()

    }

    override suspend fun updateProducts(products: List<ProductEntity>): Resource<Int> {
        val result = productDao.updateProducts(products)
        if (result == 0) return Resource.Error(resId = R.string.update_product_error)
        return Resource.Success()
    }

    override suspend fun deleteProduct(id: Int, action: PendingRemoteAction):
            Resource<Int> {
        if (action == PendingRemoteAction.COMPLETED) {
            val result = productDao.delete(id)
            if (result == 0) return Resource.Error(resId = R.string.delete_product_error)
            return Resource.Success()
        }
        val result = productDao.toPendingDelete(id)
        if (result == 0) return Resource.Error(resId = R.string.delete_product_error)
        return Resource.Success()

    }

    override suspend fun deleteAllProducts(): Resource<Int> {
        val result = productDao.deleteAll()
        if (result == 0) return Resource.Error(resId = R.string.delete_products_error)
        return Resource.Success()
    }

    private suspend fun areTheSame(product: ProductEntity): Boolean {
        try {
            val productsList = getProducts().first()
            productsList.data?.forEach {
                val hash1 = listOf(
                    it.remoteId, it.name, it.barcode, it.description, it.image,
                    it.price, it.IEPS, it.IVA, it.units,
                    DateFormat.format(it.dateTimestamp, "yyyy-MM-dd")
                ).hashCode()
                val hash2 = listOf(
                    product.remoteId, product.name, product.barcode,
                    product.description, product.image, product.price,
                    product.IEPS, product.IVA, product.units,
                    DateFormat.format(it.dateTimestamp, "yyyy-MM-dd")
                ).hashCode()
                if (hash1 == hash2) return true
            }
        } catch (e: Exception) {
            return false
        }

        return false
    }

    private suspend fun alreadyExists(remoteId: Int): Boolean {
        try {
            val productsList = getProducts().first()
            productsList.data?.forEach {
                if (remoteId == it.remoteId) return true
            }
        } catch (e: Exception) {
            return false
        }

        return false
    }

    private suspend fun preparingNewProductsToUpdate(
        newProducts: List<ProductEntity>
    ): List<ProductEntity> {

        val actualProducts = getProducts().first()
        newProducts.forEach { newProduct ->
            actualProducts.data?.forEach { actualProduct ->
                if (newProduct.remoteId == actualProduct.remoteId)
                    newProduct.id = actualProduct.id
            }
        }
        return newProducts
    }


}