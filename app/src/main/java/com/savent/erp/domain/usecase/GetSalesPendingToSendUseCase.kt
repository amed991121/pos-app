package com.savent.erp.domain.usecase

import com.savent.erp.data.local.datasource.SalesLocalDatasource
import com.savent.erp.data.local.model.SaleEntity
import com.savent.erp.utils.IsFromToday
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.first

class GetSalesPendingToSendUseCase(private val localDatasource: SalesLocalDatasource) {

    suspend operator fun invoke(): Resource<List<SaleEntity>> {
        val sales = localDatasource.getSales().first()
        return if (sales is Resource.Success) {
            val pendingTransactions: List<SaleEntity> = sales.data?.filter { saleEntity ->
                saleEntity.pendingRemoteAction != PendingRemoteAction.COMPLETED && IsFromToday(
                    saleEntity.dateTimestamp
                )
            }?: listOf()
            Resource.Success(pendingTransactions)
        } else {
            sales
        }
    }
}