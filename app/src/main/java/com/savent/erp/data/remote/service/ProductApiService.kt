package com.savent.erp.data.remote.service

import com.savent.erp.AppConstants
import com.savent.erp.data.remote.model.Client
import com.savent.erp.data.remote.model.Product
import retrofit2.Response
import retrofit2.http.*

interface ProductApiService {

    @POST(AppConstants.PRODUCTS_API_PATH)
    suspend fun insertProduct(
        @Query("businessId") businessId: Int,
        @Body product: Product
    ): Response<Int>

    @GET(AppConstants.PRODUCTS_API_PATH)
    suspend fun getProducts(
        @Query("storeId") storeId: Int,
        @Query("companyId") companyId: Int,
        @Query("filter") filter: String
    ): Response<List<Product>>

    @PUT(AppConstants.PRODUCTS_API_PATH)
    suspend fun updateProduct(
        @Query("businessId") businessId: Int,
        @Body product: Product
    ): Response<Int>

    @DELETE(AppConstants.PRODUCTS_API_PATH)
    suspend fun deleteProduct(
        @Query("businessId") businessId: Int,
        @Query("id") id: Int
    ): Response<Int>
}