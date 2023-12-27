package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.ProductsRepository
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.first

class ReloadProductsUseCase(private val productsRepository: ProductsRepository) {

    suspend operator fun invoke(
        storeId: Int,
        companyId: Int,
        filter: String
    ): Resource<Int> =
        productsRepository.fetchProducts(storeId,companyId, filter)

}