package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.CredentialsRepository
import com.savent.erp.utils.Resource

class IsLoggedUseCase(private val credentialsRepository: CredentialsRepository) {

    suspend operator fun invoke(): Boolean{
        val credentials = credentialsRepository.retrieveCredentials()
        return credentials is Resource.Success && credentials.data != null
    }
}