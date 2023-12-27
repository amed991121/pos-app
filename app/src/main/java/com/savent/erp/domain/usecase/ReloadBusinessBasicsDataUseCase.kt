package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.utils.Resource

class ReloadBusinessBasicsDataUseCase(
    private val businessBasicsRepository: BusinessBasicsRepository
) {

    suspend operator fun invoke(businessId: Int): Resource<Int> =
        businessBasicsRepository.fetchBusinessBasics(businessId)

}