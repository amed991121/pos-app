package com.savent.erp.data.local.datasource

import com.savent.erp.data.local.model.SaleEntity
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SalesLocalDatasource {

    suspend fun insertSale(sale: SaleEntity): Resource<Int>

    suspend fun insertSales(sales: List<SaleEntity>): Resource<Int>

    suspend fun getSale(id: Int): Resource<SaleEntity>

    suspend fun getSaleByRemoteId(remoteId: Int): Resource<SaleEntity>

    fun getSales(): Flow<Resource<List<SaleEntity>>>

    suspend fun updateSale(sale: SaleEntity): Resource<Int>


}