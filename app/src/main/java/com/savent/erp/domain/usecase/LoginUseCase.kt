package com.savent.erp.domain.usecase


import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.remote.model.Business
import com.savent.erp.domain.repository.BusinessRepository
import com.savent.erp.domain.repository.CredentialsRepository
import com.savent.erp.data.remote.model.LoginCredentials
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class LoginUseCase(
    private val businessRepository: BusinessRepository,
    private val credentialsRepository: CredentialsRepository
) {

    operator fun invoke(loginCredentials: LoginCredentials, storeId: Int, companyId: Int):
            Flow<Resource<Business>> = flow {

        emit(Resource.Loading())

        when (val response =
            businessRepository.fetchBusiness(loginCredentials, storeId, companyId)) {
            is Resource.Success -> {
                credentialsRepository.insertCredentials(loginCredentials)
                emit(Resource.Success())
            }
            is Resource.Error -> {
                when (response.message) {
                    "ConnectionError" -> {
                        emit(
                            Resource.Error(
                                resId = R.string.connection_error,
                            )
                        )
                    }
                    else -> {
                        emit(
                            Resource.Error(
                                resId = R.string.match_login_credentials_error,
                                message = response.message
                            )
                        )
                    }
                }

            }
            else -> {
            }
        }

    }


}