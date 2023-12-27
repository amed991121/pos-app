package com.savent.erp.domain.repository

import com.savent.erp.data.common.model.Discount
import com.savent.erp.utils.Resource

interface DiscountsRepository {

    suspend fun insertDiscounts(discounts: List<Discount>): Resource<Int>

    suspend fun getDiscount(productId: Int, clientId: Int): Resource<Discount>

    suspend fun fetchDiscounts(
        storeId: Int,
        clientId: Int,
    ): Resource<Int>

    suspend fun deleteDiscounts(clientId: Int): Resource<Int>
}