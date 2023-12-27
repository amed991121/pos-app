package com.savent.erp.data.remote.datasource

import com.savent.erp.utils.Resource
import com.savent.erp.data.remote.model.Client

interface ClientsRemoteDatasource {

    suspend fun insertClient(sellerId: Int, storeId: Int, client: Client, companyId: Int):
            Resource<Int>

    suspend fun getClients(sellerId: Int, storeId: Int?, companyId: Int, category: String):
            Resource<List<Client>>

    suspend fun updateClient(
        sellerId: Int,
        storeId: Int,
        client: Client,
        companyId: Int
    ): Resource<Int>

    suspend fun deleteClient(sellerId: Int, id: Int): Resource<Int>

}