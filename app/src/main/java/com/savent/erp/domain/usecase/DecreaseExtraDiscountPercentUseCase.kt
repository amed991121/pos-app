package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.utils.Resource

class DecreaseExtraDiscountPercentUseCase(
    private val salesRepository: SalesRepository,
    private val computePendingSalePriceUseCase: ComputePendingSalePriceUseCase
) {

    suspend operator fun invoke(): Resource<Int> {
        salesRepository.decreaseExtraDiscountPercent().let {
            if (it is Resource.Error) return it
        }
        return computePendingSalePriceUseCase()
    }


}