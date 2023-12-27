package com.savent.erp.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.remote.model.Product
import com.savent.erp.data.remote.service.ProductApiService
import com.savent.erp.utils.Resource

class ProductsRemoteDatasourceImpl(
    private val productApiService: ProductApiService
) : ProductsRemoteDatasource {

    override suspend fun insertProduct(businessId: Int, product: Product): Resource<Int> {
        try {
            val response = productApiService.insertProduct(businessId, product)
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.add_product_error)
        } catch (e: Exception) {
            //Log.d("log_",e.toString())
            return Resource.Error(message = "Error al conectar")
        }

    }

    override suspend fun getProducts(
        storeId: Int,
        companyId: Int,
        filter: String
    ):
            Resource<List<Product>> {
        try {
            val response =
                productApiService.getProducts(storeId, companyId, filter)
            //Log.d("log_",response.toString())
            //Log.d("log_",Gson().toJson(response.body()))
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.get_products_error)
        } catch (e: Exception) {
            return Resource.Error(resId = R.string.sync_input_error, message = "Error al conectar")
        }
    }

    override suspend fun updateProduct(businessId: Int, product: Product): Resource<Int> {
        try {
            val response = productApiService.updateProduct(businessId, product)
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.update_product_error)
        } catch (e: Exception) {
            Log.d("log_", e.toString())
            return Resource.Error(message = "Error al conectar")
        }

    }

    override suspend fun deleteProduct(businessId: Int, id: Int): Resource<Int> {
        try {
            val response = productApiService.deleteProduct(businessId, id)
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(resId = R.string.get_products_error)
        } catch (e: Exception) {
            Log.d("log_", e.toString())
            return Resource.Error(message = "Error al conectar")
        }
    }
}