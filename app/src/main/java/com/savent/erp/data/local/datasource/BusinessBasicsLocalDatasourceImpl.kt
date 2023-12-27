package com.savent.erp.data.local.datasource

import com.savent.erp.utils.Resource
import com.savent.erp.data.local.model.BusinessBasicsLocal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class BusinessBasicsLocalDatasourceImpl(
    private val dataObjectStorage: DataObjectStorage<BusinessBasicsLocal>
) : BusinessBasicsLocalDatasource {

    override suspend fun insertBusinessBasics(business: BusinessBasicsLocal): Resource<Int>  {
       return dataObjectStorage.saveData(business)

    }

    override fun getBusinessBasics(): Flow<Resource<BusinessBasicsLocal>> = flow {
        dataObjectStorage.getData().onEach { emit(it) }.collect()
    }

    override suspend fun updateBusinessBasics(business: BusinessBasicsLocal): Resource<Int> =
        dataObjectStorage.saveData(business)

    override suspend fun deleteBusinessBasics(): Resource<Int> =
        dataObjectStorage.clear()


}