package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.utils.Resource

class CreatePendingSaleUseCase(private val salesRepository: SalesRepository) {

    suspend operator fun invoke(): Resource<Int> =
        salesRepository.createPendingSale()


}