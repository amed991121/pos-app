package com.savent.erp.domain.usecase
import android.util.Log
import com.savent.erp.presentation.ui.model.SalesBalance
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class GetSalesBalanceUseCase(private val getSalesOfDayUseCase: GetSalesOfDayUseCase) {

    suspend operator fun invoke(): Flow<Resource<SalesBalance>> = flow {
        getSalesOfDayUseCase("").onEach {
            if (it is Resource.Error){
                emit(Resource.Error())
                return@onEach
            }
            var count = 0
            it.data?.forEach { saleItem->
                if (saleItem.isPendingSending) count++
            }

            emit(Resource.Success(SalesBalance(count,it.data?.size?:0)))
        }.collect()
    }
}