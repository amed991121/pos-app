package com.savent.erp.domain.repository

import com.savent.erp.utils.Resource
import com.savent.erp.data.remote.model.Business
import com.savent.erp.data.remote.model.LoginCredentials

interface BusinessRepository {

    suspend fun insertBusiness(business: Business): Resource<Int>

    suspend fun fetchBusiness(credentials: LoginCredentials, storeId: Int, companyId: Int):
            Resource<Business>

}