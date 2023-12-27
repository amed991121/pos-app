package com.savent.erp.data.repository

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.local.model.BusinessBasicsLocal
import com.savent.erp.data.local.datasource.BusinessBasicsLocalDatasource
import com.savent.erp.data.remote.datasource.BusinessBasicsRemoteDatasource
import com.savent.erp.data.remote.model.BusinessBasics
import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*
import java.lang.Exception

class BusinessBasicsRepositoryImpl(
    private val localDatasource: BusinessBasicsLocalDatasource,
    private val remoteDatasource: BusinessBasicsRemoteDatasource
) : BusinessBasicsRepository {

    override suspend fun insertBusinessBasics(business: BusinessBasics): Resource<Int> =
        localDatasource.insertBusinessBasics(mapToLocal(business, PendingRemoteAction.COMPLETED))


    override fun getBusinessBasics(): Flow<Resource<BusinessBasicsLocal>> = flow {
        localDatasource.getBusinessBasics().onEach { emit(it) }.collect()
    }


    override suspend fun fetchBusinessBasics(id: Int): Resource<Int> {
        val response = remoteDatasource.getBusinessBasics(id)
        if (response is Resource.Success) {
            response.data?.let {
                localDatasource.insertBusinessBasics(mapToLocal(it, PendingRemoteAction.INSERT))
            }
            return Resource.Success()
        }
        return Resource.Error(resId = response.resId)
    }

    override suspend fun updateBusinessBasics(id: Int, business: BusinessBasics): Resource<Int> =
        localDatasource.updateBusinessBasics(mapToLocal(business, PendingRemoteAction.UPDATE))


    private fun mapToLocal(businessBasics: BusinessBasics, action: PendingRemoteAction)
            : BusinessBasicsLocal {
        return BusinessBasicsLocal(
            businessBasics.id,
            businessBasics.name,
            businessBasics.sellerId,
            businessBasics.sellerName,
            businessBasics.sellerLevel,
            businessBasics.storeId,
            businessBasics.storeName,
            businessBasics.companyId,
            businessBasics.location,
            businessBasics.address,
            businessBasics.image,
            businessBasics.receiptFooter,
            action
        )
    }


}