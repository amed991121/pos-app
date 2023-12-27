package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.utils.Resource

class RemoveProductFromSaleUseCase(
    private val salesRepository: SalesRepository,
    private val computePendingSalePriceUseCase: ComputePendingSalePriceUseCase
) {

    suspend operator fun invoke(productId: Int): Resource<Int> {
        salesRepository.removeProductFromPendingSale(productId).let {
            if (it is Resource.Error) return it
        }
        return computePendingSalePriceUseCase()
    }
}