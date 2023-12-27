package com.savent.erp.data.local.datasource

import com.savent.erp.R
import com.savent.erp.data.remote.model.LoginCredentials
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*


class CredentialsLocalDatasourceImpl(
    private val dataObjectStorage: DataObjectStorage<LoginCredentials>
) : CredentialsLocalDatasource {

    override suspend fun insertCredentials(credentials: LoginCredentials): Resource<Int> {
        return dataObjectStorage.saveData(credentials)
    }

    override suspend fun getCredentials(): Resource<LoginCredentials> {
        var credentials: LoginCredentials? = null
        try {
            credentials = dataObjectStorage.getData().first().data
        } catch (e: Exception) {
            return Resource.Error(resId = R.string.fetch_login_credentials_error)
        }
        return Resource.Success(credentials)
    }

    override suspend fun deleteCredentials(): Resource<Int> {
        return dataObjectStorage.clear()
    }


}