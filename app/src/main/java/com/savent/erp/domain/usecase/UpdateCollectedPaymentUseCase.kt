package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.utils.Resource

class UpdateCollectedPaymentUseCase(
    private val salesRepository: SalesRepository

) {

    suspend operator fun invoke(collected: Float): Resource<Int> {
        return salesRepository.updatePaymentCollected(collected)
    }


}