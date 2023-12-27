package com.savent.erp.domain.usecase

import com.savent.erp.R
import com.savent.erp.data.common.model.Purchase
import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.domain.repository.ProductsRepository
import com.savent.erp.domain.repository.PurchasesRepository
import com.savent.erp.presentation.ui.model.ProductItem
import com.savent.erp.utils.NameFormat
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAvailableProductsFromPurchaseUseCase(
    private val productsRepository: ProductsRepository,
    private val purchasesRepository: PurchasesRepository
) {

    operator fun invoke(purchaseId: Int, query: String, selectedProducts: HashMap<Int,Int>): Flow<Resource<List<ProductItem>>> = flow {
        val products = mutableListOf<ProductItem>()
        val purchase = purchasesRepository.getPurchase(purchaseId).data ?: kotlin.run {
            emit(Resource.Error(resId = R.string.get_purchases_error))
            return@flow
        }
        for ((productId, units) in purchase.remainingProducts) {
            val product =
                productsRepository.getProductByRemoteId(productId.toLong()).data ?: kotlin.run {
                    emit(Resource.Error(resId = R.string.get_products_error))
                    return@flow
                }
            products.add(mapToUiItem(product, units, selectedProducts[productId]?:0))
        }
        emit(Resource.Success(products.filter { it.name.contains(query, ignoreCase = true) }))
    }

    private fun mapToUiItem(product: ProductEntity, totalUnits: Int, selectedUnits: Int) =
        ProductItem(
            product.remoteId,
            NameFormat.format(product.name),
            product.image,
            product.description,
            product.price,
            selectedUnits,
            totalUnits - selectedUnits
        )
}