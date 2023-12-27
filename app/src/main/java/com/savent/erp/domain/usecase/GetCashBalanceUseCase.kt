package com.savent.erp.domain.usecase

import com.savent.erp.presentation.ui.model.CashBalance
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class GetCashBalanceUseCase(private val getSalesOfDayUseCase: GetSalesOfDayUseCase) {

    operator fun invoke(): Flow<Resource<CashBalance>> = flow {
        getSalesOfDayUseCase("").onEach {
            var revenues = 0F
            var debts = 0F

            if (it is Resource.Error){
                emit(Resource.Error())
                return@onEach
            }
            it.data?.forEach { saleItem->
                (saleItem.toPay - saleItem.collected ).let { it1->
                    if(it1 <= 0){
                        revenues += saleItem.toPay
                    }else {
                        revenues += saleItem.collected
                        debts += it1
                    }
                }
            }
            emit(Resource.Success(CashBalance(revenues,debts)))
        }.collect()
    }
}