package com.savent.erp.data.remote.datasource

import com.savent.erp.R
import com.savent.erp.data.local.model.BusinessBasicsLocal
import com.savent.erp.data.remote.model.BusinessBasics
import com.savent.erp.data.remote.service.BusinessBasicsApiService
import com.savent.erp.utils.Resource

class BusinessBasicsRemoteDatasourceImpl(
    private val businessApiService: BusinessBasicsApiService
) : BusinessBasicsRemoteDatasource {

    override suspend fun getBusinessBasics(id: Int): Resource<BusinessBasics> {
        val response = businessApiService.getBusinessBasics(id)
        if (response.isSuccessful)
            return Resource.Success(response.body())
        return Resource.Error(resId = R.string.get_business_error)
    }

    override suspend fun updateBusinessBasics(id: Int, businessBasics: BusinessBasics)
            : Resource<Int> {
        val response = businessApiService.updateBasics(id, businessBasics)
        if (response.isSuccessful)
            return Resource.Success(response.body())
        return Resource.Error(resId = R.string.update_business_error)
    }
}