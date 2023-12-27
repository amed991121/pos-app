package com.savent.erp.domain.usecase


import com.savent.erp.R
import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.data.local.model.SaleEntity
import com.savent.erp.domain.repository.ClientsRepository
import com.savent.erp.domain.repository.ProductsRepository
import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.utils.NameFormat
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.first

class SaveCompletedSaleUseCase(
    private val salesRepository: SalesRepository,
    private val clientRepository: ClientsRepository,
    private val productsRepository: ProductsRepository
) {

    suspend operator fun invoke(): Resource<Int> {
        val pendingSale = salesRepository.getPendingSale().first()

        if (pendingSale is Resource.Success) {
            pendingSale.data?.let {
                val client = clientRepository.getClient(it.clientId)
                val updatedProducts = mutableListOf<ProductEntity>()
                val selectedProducts = HashMap<Int, Int>()

                it.selectedProducts.entries.forEach { it1 ->
                    val product = productsRepository.getProduct(it1.key)
                    if (product is Resource.Error)
                        return Resource.Error(resId = product.resId)

                    product.data?.let { productEntity ->
                        selectedProducts[productEntity.remoteId] = it1.value
                        productEntity.units = productEntity.units - it1.value
                        productEntity.pendingRemoteAction = PendingRemoteAction.UPDATE
                        updatedProducts.add(productEntity)
                    } ?: Resource.Error<Int>(resId = R.string.unknown_error)

                }
                var name = NameFormat.format(client.data?.name + " ${client.data?.paternalName} " +
                        "${client.data?.maternalName}")
                client.data?.tradeName?.let { it1-> if(it1.isNotEmpty())
                    name = "${NameFormat.format(it1)} ($name)" }

                val sale = SaleEntity(
                    0,
                    Integer.MAX_VALUE,
                    client.data?.remoteId ?: 0,
                    name,
                    System.currentTimeMillis(),
                    selectedProducts,
                    it.subtotal,
                    it.discounts,
                    it.IVA,
                    it.IEPS,
                    it.extraDiscountPercent,
                    it.collected,
                    it.total,
                    it.paymentMethod
                )
                var result = salesRepository.updatePendingSale(sale)
                if (result is Resource.Error) return result

                result = salesRepository.insertSale(sale)
                if (result is Resource.Error) return result

                return productsRepository.updateProducts(updatedProducts)
            }

        }
        return Resource.Error(resId = pendingSale.resId)
    }

}