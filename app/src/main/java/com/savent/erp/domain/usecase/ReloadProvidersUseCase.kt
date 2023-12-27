package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.ProvidersRepository
import com.savent.erp.utils.Resource

class ReloadProvidersUseCase(private val providersRepository: ProvidersRepository) {

    suspend operator fun invoke(companyId: Int): Resource<Int> =
        providersRepository.fetchProviders(companyId)
}