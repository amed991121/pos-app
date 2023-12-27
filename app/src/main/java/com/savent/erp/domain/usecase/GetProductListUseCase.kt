package com.savent.erp.domain.usecase

import com.savent.erp.R
import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.data.local.model.SaleEntity
import com.savent.erp.domain.repository.ProductsRepository
import com.savent.erp.presentation.ui.model.ProductItem
import com.savent.erp.utils.DateFormat
import com.savent.erp.utils.NameFormat
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class GetProductListUseCase(
    private val productsRepository: ProductsRepository,
    private val getPendingSaleUseCase: GetPendingSaleUseCase
) {

    suspend operator fun invoke(query: String): Flow<Resource<List<ProductItem>>> = flow {

        getProductsFlow(query).combine(getPendingSaleUseCase()) { products, pendingSale ->
            if (products is Resource.Success && products.data != null)
                emit(Resource.Success(products.data.map { mapToUiItem(it, pendingSale.data) }))
            else
                emit(Resource.Error<List<ProductItem>>(resId = R.string.get_products_error))
        }.collect()


    }

    private fun getProductsFlow(query: String): Flow<Resource<List<ProductEntity>>> = flow {
        productsRepository.getProducts(query).onEach {
            if (it is Resource.Success)
                it.data?.let { it1 ->
                    emit(Resource.Success(it1.filter { productEntity ->
                        val today = DateFormat.format(System.currentTimeMillis(), "yyyy-MM-dd")
                        val currentDate = DateFormat.format(productEntity.dateTimestamp, "yyyy-MM-dd")
                        productEntity.units > 0 && today == currentDate
                    }))
                } ?: emit(Resource.Error<List<ProductEntity>>(resId = R.string.get_products_error))
            else
                emit(Resource.Error<List<ProductEntity>>(resId = R.string.get_products_error))

        }.collect()
    }

    private fun mapToUiItem(product: ProductEntity, pendingSale: SaleEntity?): ProductItem {
        val selectedUnits = pendingSale?.let { it.selectedProducts[product.id] ?: 0 } ?: 0
        val remainingUnits = product.units - selectedUnits

        return ProductItem(
            product.id,
            NameFormat.format(product.name),
            product.image,
            product.description,
            product.price,
            selectedUnits,
            remainingUnits
        )
    }
}