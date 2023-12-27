package com.savent.erp.data.remote.datasource

import com.savent.erp.data.local.model.BusinessBasicsLocal
import com.savent.erp.data.remote.model.BusinessBasics
import com.savent.erp.utils.Resource

interface BusinessBasicsRemoteDatasource {

    suspend fun getBusinessBasics(id: Int): Resource<BusinessBasics>

    suspend fun updateBusinessBasics(id: Int, businessBasics: BusinessBasics)
            : Resource<Int>

}