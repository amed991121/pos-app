package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddProductToSaleUseCase(
    private val salesRepository: SalesRepository,
    private val computePendingSalePriceUseCase: ComputePendingSalePriceUseCase
) {

    suspend operator fun invoke(productId: Int): Resource<Int> {
        salesRepository.addProductToPendingSale(productId).let {
            if (it is Resource.Error) return it
        }
        return computePendingSalePriceUseCase()
    }


}