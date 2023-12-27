package com.savent.erp.data.remote.datasource

import com.savent.erp.data.common.model.Purchase
import com.savent.erp.utils.Resource

interface PurchaseRemoteDatasource {
    suspend fun getPurchases(
        companyId: Int
    ): Resource<List<Purchase>>
}