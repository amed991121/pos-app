package com.savent.erp.data.local.datasource

import com.savent.erp.data.common.model.Discount
import com.savent.erp.utils.Resource

interface DiscountsLocalDatasource {

    suspend fun insertDiscounts(discounts: List<Discount>): Resource<Int>

    suspend fun getDiscount(productId: Int, clientId: Int): Resource<Discount>

    suspend fun deleteDiscounts(clientId: Int): Resource<Int>
}