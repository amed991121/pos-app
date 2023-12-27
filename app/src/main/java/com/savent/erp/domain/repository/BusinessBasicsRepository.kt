package com.savent.erp.domain.repository

import com.savent.erp.data.local.model.BusinessBasicsLocal
import com.savent.erp.data.remote.model.BusinessBasics
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BusinessBasicsRepository {

    suspend fun insertBusinessBasics(business: BusinessBasics): Resource<Int>

    fun getBusinessBasics(): Flow<Resource<BusinessBasicsLocal>>

    suspend fun fetchBusinessBasics(id:Int): Resource<Int>

    suspend fun updateBusinessBasics(id: Int, business: BusinessBasics): Resource<Int>

}