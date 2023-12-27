package com.savent.erp.domain.usecase

import com.savent.erp.data.local.model.SaleEntity
import com.savent.erp.domain.repository.ClientsRepository
import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.presentation.ui.model.Checkout
import com.savent.erp.utils.NameFormat
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class GetCheckoutSaleUseCase(
    private val salesRepository: SalesRepository,
    private val clientRepository: ClientsRepository,
) {

    operator fun invoke(): Flow<Resource<Checkout>> = flow {
        emit(Resource.Loading())
        salesRepository.getPendingSale().onEach {

            when(it){
                is Resource.Success ->{
                    it.data?.let { it1 -> emit(Resource.Success(mapToUiModel(it1))) }
                }
                is Resource.Error -> {
                    emit(Resource.Error<Checkout>(resId = it.resId))
                }
                else -> {}
            }

        }.collect()
    }

    private suspend fun mapToUiModel(sale: SaleEntity): Checkout {
        val client = clientRepository.getClient(sale.clientId)

        val subtotal1 = sale.subtotal - sale.discounts
        val extraDiscount = (sale.extraDiscountPercent / 100 * 0F)  * subtotal1
        val taxes =  sale.IVA + sale.IEPS
        val finalPrice = subtotal1 - extraDiscount + taxes

        var name = NameFormat.format(client.data?.name + " ${client.data?.paternalName} " +
                "${client.data?.maternalName}");
        client.data?.tradeName?.let { it1-> if(it1.isNotEmpty())
            name = "${NameFormat.format(it1)} ($name)"; }

        return Checkout(
            name,
            client.data?.image,
            sale.selectedProducts.size,
            sale.subtotal,
            taxes,
            sale.discounts + extraDiscount,
            finalPrice,
            sale.extraDiscountPercent,
            sale.paymentMethod,
            sale.collected,
           0F
        )
    }


}