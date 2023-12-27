package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.utils.Resource

class UpdateExtraDiscountPercentUseCase(
    private val salesRepository: SalesRepository,
    private val computePendingSalePriceUseCase: ComputePendingSalePriceUseCase
) {

    suspend operator fun invoke(discount: Int): Resource<Int> {
        salesRepository.updateExtraDiscountPercent(discount).let {
            if (it is Resource.Error) return it
        }
        return computePendingSalePriceUseCase()
    }


}