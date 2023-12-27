package com.savent.erp.domain.usecase

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.presentation.ui.model.Balance
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class ComputeBalanceUseCase(private val getSalesOfDayUseCase: GetSalesOfDayUseCase) {

    operator fun invoke(): Flow<Resource<Balance>> = flow {
        var revenues = 0F
        var debts = 0F

        getSalesOfDayUseCase().onEach {
            if (it is Resource.Error){
                emit(Resource.Error())
                return@onEach
            }
            it.data?.forEach { saleItem->
                revenues += saleItem.collected - saleItem.change
                debts += saleItem.toPay - (saleItem.collected - saleItem.change)
            }
            emit(Resource.Success(Balance(revenues,debts)))
        }.collect()
    }
}