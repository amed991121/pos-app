package com.savent.erp.domain.repository

import com.savent.erp.data.remote.model.LoginCredentials
import com.savent.erp.utils.Resource

interface CredentialsRepository {

    suspend fun insertCredentials(credentials: LoginCredentials): Resource<Int>

    suspend fun retrieveCredentials(): Resource<LoginCredentials>

    suspend fun deleteCredentials(): Resource<Int>

}