package com.savent.erp.domain.usecase

import com.savent.erp.R
import com.savent.erp.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteDataSyncFromLocalUseCase{

    suspend operator fun invoke(
        businessId: Int,
        sellerId: Int,
        storeId: Int,
        companyId: Int
    ): Flow<Resource<Int>> = flow {

        coroutineScope {
            val clientsSync =
                async { RemoteClientSyncFromLocalUseCase.execute(sellerId, storeId, companyId) }

            val salesSync =
                async { RemoteSaleSyncFromLocalUseCase.execute(businessId, sellerId, storeId, companyId) }

            val debtPaymentsSync = async {
                RemoteDebtPaymentSyncFromLocalUseCase.execute(
                    businessId,
                    sellerId,
                    storeId,
                    companyId
                )
            }

            if(clientsSync.await() is Resource.Error){
                emit(Resource.Error(resId = R.string.sync_clients_error))
                return@coroutineScope
            }

            if(salesSync.await() is Resource.Error){
                emit(Resource.Error(resId = R.string.sync_sales_error))
                return@coroutineScope
            }

            if(debtPaymentsSync.await() is Resource.Error){
                emit(Resource.Error(resId = R.string.sync_debt_payments_error))
                return@coroutineScope
            }

            emit(Resource.Success(0))

        }
    }
}