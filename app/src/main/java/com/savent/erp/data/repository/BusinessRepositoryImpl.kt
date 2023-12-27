package com.savent.erp.data.repository

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.utils.Resource
import com.savent.erp.data.remote.datasource.BusinessRemoteDatasource
import com.savent.erp.data.remote.model.Business
import com.savent.erp.domain.repository.*
import com.savent.erp.data.remote.model.LoginCredentials

class BusinessRepositoryImpl(
    private val businessBasicsRepository: BusinessBasicsRepository,
    private val businessRemoteDatasource: BusinessRemoteDatasource,
) : BusinessRepository {

    override suspend fun insertBusiness(business: Business): Resource<Int> {
        val resultBasics =
            businessBasicsRepository.insertBusinessBasics(business.basics)

        if (resultBasics is Resource.Error || resultBasics.data == null)
            return Resource.Error(resId = R.string.insert_business_error)

        return Resource.Success()
    }

    override suspend fun fetchBusiness(credentials: LoginCredentials, storeId: Int, companyId: Int):
            Resource<Business> {
        val response = businessRemoteDatasource.getBusiness(credentials, storeId, companyId)
        if (response is Resource.Success) {
            response.data?.let {
                insertBusiness(it)
            }
            return response
        }
        return Resource.Error(resId = response.resId, message = response.message)

    }


}