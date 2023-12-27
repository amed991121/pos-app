package com.savent.erp.domain.usecase

import com.savent.erp.R
import com.savent.erp.presentation.ui.model.ProductItem
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class GetSelectedProductsUseCase(
    private val getProductListUseCase: GetProductListUseCase,
    private val getPendingSaleUseCase: GetPendingSaleUseCase
) {
    suspend operator fun invoke(): Flow<Resource<List<ProductItem>>> = flow {

        getPendingSaleUseCase().combine(getProductListUseCase("")) { pendingSale, products ->
            if (products is Resource.Success && pendingSale is Resource.Success
                && products.data != null && pendingSale.data != null
            ) {
                val idsSelected = pendingSale.data.selectedProducts
                emit(Resource.Success(products.data.filter { productItem ->
                    idsSelected.containsKey(productItem.id) && productItem.selectedUnits != 0
                }))
            }else
                emit(Resource.Error<List<ProductItem>>(resId = R.string.get_products_error))

        }.collect()


    }
}