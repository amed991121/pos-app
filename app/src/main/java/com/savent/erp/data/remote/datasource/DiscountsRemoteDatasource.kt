package com.savent.erp.data.remote.datasource

import com.savent.erp.data.common.model.Discount
import com.savent.erp.utils.Resource

interface DiscountsRemoteDatasource {

    suspend fun getDiscounts(
        storeId: Int,
        clientId: Int,
    ): Resource<List<Discount>>

}