package com.savent.erp.data.repository

import com.savent.erp.utils.Resource
import com.savent.erp.data.local.datasource.CredentialsLocalDatasource
import com.savent.erp.domain.repository.CredentialsRepository
import com.savent.erp.data.remote.model.LoginCredentials

class CredentialsRepositoryImpl(
    private val localDatasource: CredentialsLocalDatasource
) : CredentialsRepository {

    override suspend fun insertCredentials(credentials: LoginCredentials): Resource<Int> =
        localDatasource.insertCredentials(credentials)


    override suspend fun retrieveCredentials(): Resource<LoginCredentials> =
        localDatasource.getCredentials()



    override suspend fun deleteCredentials(): Resource<Int> =
        localDatasource.deleteCredentials()


}