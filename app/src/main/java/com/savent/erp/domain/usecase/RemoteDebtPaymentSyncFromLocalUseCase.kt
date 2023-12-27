package com.savent.erp.domain.usecase

import com.savent.erp.R
import com.savent.erp.data.local.datasource.DebtPaymentLocalDatasource
import com.savent.erp.data.local.model.DebtPaymentEntity
import com.savent.erp.data.remote.datasource.DebtPaymentRemoteDatasource
import com.savent.erp.data.remote.model.DebtPayment
import com.savent.erp.utils.DateFormat
import com.savent.erp.utils.DateTimeObj
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.java.KoinJavaComponent.inject


class RemoteDebtPaymentSyncFromLocalUseCase {

    companion object {

        private val localDatasource: DebtPaymentLocalDatasource by inject(
            DebtPaymentLocalDatasource::class.java
        )
        private val remoteDatasource: DebtPaymentRemoteDatasource by inject(
            DebtPaymentRemoteDatasource::class.java
        )
        private val pendingTransactions: GetDebtPaymentsPendingToSendUseCase by inject(
            GetDebtPaymentsPendingToSendUseCase::class.java
        )

        fun execute(
            businessId: Int,
            sellerId: Int,
            storeId: Int,
            companyId: Int
        ): Resource<Int> =
            synchronized(this) {
                runBlocking(Dispatchers.IO){
                    pendingTransactions().let {
                        if (it is Resource.Success) {
                            if (it.data?.isEmpty() == true)  Resource.Success(0)
                            it.data?.let { list ->
                                list.forEach { debtEntity ->
                                    executeTransaction(
                                        businessId,
                                        sellerId,
                                        storeId,
                                        debtEntity,
                                        companyId
                                    )
                                }
                            }
                        }
                    }

                    pendingTransactions().let {
                        if (it is Resource.Error || it.data?.isNotEmpty() == true)
                            Resource.Error<Int>(resId = R.string.sync_output_error)
                        Resource.Success(0)
                    }
                }

            }


        private suspend fun executeTransaction(
            businessId: Int, sellerId: Int, storeId: Int,
            debtPaymentEntity: DebtPaymentEntity, companyId: Int
        ) {
            when (debtPaymentEntity.pendingRemoteAction) {
                PendingRemoteAction.INSERT -> {
                    val response = remoteDatasource.insertDebtPayment(
                        businessId,
                        sellerId,
                        storeId,
                        mapToApiModel(debtPaymentEntity),
                        companyId
                    )
                    if (response is Resource.Success) {
                        debtPaymentEntity.pendingRemoteAction = PendingRemoteAction.COMPLETED
                        debtPaymentEntity.remoteId = response.data?:0
                        localDatasource.updateDebtPayment(debtPaymentEntity)
                    }
                }

                else -> {
                }
            }
        }

        private fun mapToApiModel(debtPaymentEntity: DebtPaymentEntity): DebtPayment {

            return DebtPayment(
                0,
                debtPaymentEntity.saleId,
                debtPaymentEntity.clientId,
                DateTimeObj.fromLong(debtPaymentEntity.saleTimestamp),
                DateTimeObj.fromLong(debtPaymentEntity.dateTimestamp),
                debtPaymentEntity.toPay,
                debtPaymentEntity.paid,
                debtPaymentEntity.paymentMethod,
            )
        }
    }



}