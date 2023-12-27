package com.savent.erp.data.remote.datasource


import com.savent.erp.data.remote.model.Business
import com.savent.erp.data.remote.model.LoginCredentials
import com.savent.erp.utils.Resource


interface BusinessRemoteDatasource {

    suspend fun getBusiness(
        credentials: LoginCredentials,
        storeId: Int,
        companyId: Int
    ): Resource<Business>

}