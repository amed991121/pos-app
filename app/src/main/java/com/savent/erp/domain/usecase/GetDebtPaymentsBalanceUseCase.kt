package com.savent.erp.domain.usecase

import android.util.Log
import com.savent.erp.presentation.ui.model.DebtPaymentsBalance
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class GetDebtPaymentsBalanceUseCase(private val getDebtPaymentsOfDayUseCase: GetDebtPaymentsOfDayUseCase) {

    operator fun invoke(): Flow<Resource<DebtPaymentsBalance>> = flow {
        getDebtPaymentsOfDayUseCase("").onEach {
            if (it is Resource.Error) {
                emit(Resource.Error(resId = it.resId))
                return@onEach
            }
            var pendingToSend = 0
            var total = 0
            var totalCollected = 0F
            it.data?.forEach { item ->
                total++
                totalCollected += item.collected
                if (item.isPendingSending) pendingToSend++
            }

            emit(Resource.Success(DebtPaymentsBalance(pendingToSend,total,totalCollected)))

        }.collect()
    }
}