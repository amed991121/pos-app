package com.savent.erp.data.local.datasource

import com.savent.erp.data.remote.model.LoginCredentials
import com.savent.erp.utils.Resource

interface CredentialsLocalDatasource {

    suspend fun insertCredentials(credentials: LoginCredentials): Resource<Int>

    suspend fun getCredentials(): Resource<LoginCredentials>

    suspend fun deleteCredentials(): Resource<Int>
}