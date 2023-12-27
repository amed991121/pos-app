package com.savent.erp.domain.usecase

import com.savent.erp.R
import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.domain.repository.ProductsRepository
import com.savent.erp.presentation.ui.model.ProductItem
import com.savent.erp.utils.NameFormat
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class GetAllProductsUseCase(private val productsRepository: ProductsRepository) {

    operator fun invoke(
        query: String,
        selectedProducts: HashMap<Int, Int>
    ): Flow<Resource<List<ProductItem>>> = flow {
        productsRepository.getProducts(query).onEach { resource ->
            if (resource.data == null) {
                emit(Resource.Error(resId = R.string.get_products_error))
                return@onEach
            }
            val entities = resource.data
            emit(Resource.Success(entities.map { mapToUiItem(it, selectedProducts) }))

        }.collect()
    }

    private fun mapToUiItem(
        product: ProductEntity,
        selectedProducts: HashMap<Int, Int>
    ): ProductItem =
        ProductItem(
            product.remoteId,
            NameFormat.format(product.name),
            product.image,
            product.description,
            product.price,
            selectedProducts[product.remoteId] ?: 0,
            1000
        )
}