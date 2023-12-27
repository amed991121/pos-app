package com.savent.erp.domain.usecase

import com.savent.erp.R
import com.savent.erp.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IsDataPendingSyncUseCase(
    private val getClientsPendingToSendUseCase: GetClientsPendingToSendUseCase,
    private val getSalesPendingToSendUseCase: GetSalesPendingToSendUseCase,
    private val getDebtPaymentsPendingToSendUseCase: GetDebtPaymentsPendingToSendUseCase,
) {

    suspend operator fun invoke(): Flow<Boolean> = flow {
        coroutineScope {
            val emptyPendingClientData = async { getClientsPendingToSendUseCase() }
            val emptyPendingSaleData = async { getSalesPendingToSendUseCase() }
            val emptyPendingDebtPaymentData = async { getDebtPaymentsPendingToSendUseCase() }

            if (emptyPendingClientData.await().data?.isNotEmpty() == true
                || emptyPendingSaleData.await().data?.isNotEmpty() == true
                || emptyPendingDebtPaymentData.await().data?.isNotEmpty() == true
            )
                emit(true)
            else emit(false)

        }
    }
}