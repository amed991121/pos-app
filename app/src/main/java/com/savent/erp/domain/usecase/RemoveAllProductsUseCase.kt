package com.savent.erp.domain.usecase

import com.savent.erp.data.local.datasource.ProductsLocalDatasource
import com.savent.erp.utils.Resource

class RemoveAllProductsUseCase(private val productsLocalDatasource: ProductsLocalDatasource) {

    suspend operator fun invoke(): Resource<Int> =
        productsLocalDatasource.deleteAllProducts()
}
