package com.savent.erp.data.remote.datasource

import com.savent.erp.data.remote.model.Sale
import com.savent.erp.utils.Resource

interface SalesRemoteDatasource {

    suspend fun insertSale(
        businessId: Int,
        sellerId: Int,
        storeId: Int,
        sale: Sale,
        companyId: Int
    ): Resource<Int>

    suspend fun getSales(
        businessId: Int,
        storeId: Int,
        date: String,
        companyId: Int
    ): Resource<List<Sale>>
}