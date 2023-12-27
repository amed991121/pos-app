package com.savent.erp.domain.usecase

import com.savent.erp.data.local.datasource.BusinessBasicsLocalDatasource
import com.savent.erp.data.local.model.BusinessBasicsLocal
import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.data.remote.datasource.BusinessBasicsRemoteDatasource
import com.savent.erp.data.remote.model.BusinessBasics
import com.savent.erp.data.remote.model.Product
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class RemoteBusinessBasicsSyncFromLocalUseCase(
    private val localDatasource: BusinessBasicsLocalDatasource,
    private val remoteDatasource: BusinessBasicsRemoteDatasource
) {

    suspend operator fun invoke(businessId: Int) {
        localDatasource.getBusinessBasics().onEach {
            if (it is Resource.Success)
               it.data?.let { businessBasicsLocal ->
                   if (businessBasicsLocal.remoteAction == PendingRemoteAction.UPDATE)
                       executeTransaction(businessId,businessBasicsLocal)
               }

        }.collect()
    }

    private suspend fun executeTransaction(
        businessId: Int, businessBasicsLocal: BusinessBasicsLocal
    ) {
        when (businessBasicsLocal.remoteAction) {
            PendingRemoteAction.UPDATE -> {
                val response = remoteDatasource.updateBusinessBasics(
                    businessId,
                    mapToApiModel(businessBasicsLocal)
                )
                if (response is Resource.Success) {
                    businessBasicsLocal.remoteAction = PendingRemoteAction.COMPLETED
                    localDatasource.updateBusinessBasics(businessBasicsLocal)
                }
            }
            else -> {
            }
        }
    }

    private fun mapToApiModel(businessBasicsLocal: BusinessBasicsLocal): BusinessBasics {
        return BusinessBasics(
            businessBasicsLocal.id,
            businessBasicsLocal.name,
            businessBasicsLocal.sellerId,
            businessBasicsLocal.sellerName,
            businessBasicsLocal.sellerLevel,
            businessBasicsLocal.storeId,
            businessBasicsLocal.storeName,
            businessBasicsLocal.companyId,
            businessBasicsLocal.location,
            businessBasicsLocal.address,
            businessBasicsLocal.image,
            businessBasicsLocal.receiptFooter,
        )
    }
}