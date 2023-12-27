package com.savent.erp.domain.usecase

import android.util.Log
import com.savent.erp.R
import com.savent.erp.data.local.datasource.ClientsLocalDatasource
import com.savent.erp.data.local.model.ClientEntity
import com.savent.erp.data.remote.datasource.ClientsRemoteDatasource
import com.savent.erp.data.remote.model.Client
import com.savent.erp.utils.DateFormat
import com.savent.erp.utils.DateTimeObj
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.java.KoinJavaComponent.inject

class RemoteClientSyncFromLocalUseCase {

    companion object {

        private val localDatasource: ClientsLocalDatasource by inject(
            ClientsLocalDatasource::class.java
        )
        private val remoteDatasource: ClientsRemoteDatasource by inject(
            ClientsRemoteDatasource::class.java
        )
        private val pendingTransactions: GetClientsPendingToSendUseCase by inject(
            GetClientsPendingToSendUseCase::class.java
        )

        fun execute(sellerId: Int, storeId: Int, companyId: Int): Resource<Int> =
            synchronized(this){
                runBlocking {
                    pendingTransactions().let {
                        if (it is Resource.Success) {
                            if (it.data?.isEmpty() == true)  Resource.Success(0)
                            it.data?.let { list ->
                                list.forEach { clientEntity ->
                                    executeTransaction(
                                        sellerId,
                                        storeId,
                                        clientEntity,
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
            sellerId: Int,
            storeId: Int,
            clientEntity: ClientEntity,
            companyId: Int
        ) {
            when (clientEntity.pendingRemoteAction) {
                PendingRemoteAction.INSERT -> {
                    val response = remoteDatasource.insertClient(
                        sellerId,
                        storeId,
                        mapToApiModel(clientEntity),
                        companyId
                    )
                    if (response is Resource.Success) {
                        clientEntity.remoteId = response.data!!
                        clientEntity.pendingRemoteAction = PendingRemoteAction.COMPLETED
                        localDatasource.updateClient(clientEntity)
                    }
                }
                PendingRemoteAction.UPDATE -> {
                    val response = remoteDatasource.updateClient(
                        sellerId,
                        storeId,
                        mapToApiModel(clientEntity),
                        companyId
                    )
                    if (response is Resource.Success) {
                        clientEntity.pendingRemoteAction = PendingRemoteAction.COMPLETED
                        localDatasource.updateClient(clientEntity)
                    }
                }
                PendingRemoteAction.DELETE -> {
                    val response = remoteDatasource.deleteClient(
                        sellerId,
                        clientEntity.remoteId ?: 0
                    )
                    if (response is Resource.Success) {
                        localDatasource.deleteClient(clientEntity.id, PendingRemoteAction.COMPLETED)
                    }
                }
                else -> {
                }
            }
        }

        private fun mapToApiModel(clientEntity: ClientEntity): Client {
            return Client(
                clientEntity.remoteId,
                clientEntity.image,
                clientEntity.name,
                clientEntity.tradeName,
                clientEntity.paternalName,
                clientEntity.maternalName,
                clientEntity.socialReason,
                clientEntity.rfc,
                clientEntity.phoneNumber,
                clientEntity.email,
                clientEntity.street,
                clientEntity.noExterior,
                clientEntity.colonia,
                clientEntity.postalCode,
                clientEntity.city,
                clientEntity.state,
                clientEntity.country,
                clientEntity.location,
                clientEntity.creditLimit,
                clientEntity.dateTimestamp?.let { DateTimeObj.fromLong(it) }
            )
        }
    }


}