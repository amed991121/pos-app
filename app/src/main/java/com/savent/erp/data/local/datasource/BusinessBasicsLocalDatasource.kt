package com.savent.erp.data.local.datasource

import com.savent.erp.utils.Resource
import com.savent.erp.data.local.model.BusinessBasicsLocal
import kotlinx.coroutines.flow.Flow

interface BusinessBasicsLocalDatasource {

    suspend fun insertBusinessBasics(business: BusinessBasicsLocal): Resource<Int>

    fun getBusinessBasics(): Flow<Resource<BusinessBasicsLocal>>

    suspend fun updateBusinessBasics(business: BusinessBasicsLocal): Resource<Int>

    suspend fun deleteBusinessBasics(): Resource<Int>



}