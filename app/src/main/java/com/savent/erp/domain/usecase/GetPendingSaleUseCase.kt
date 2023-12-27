package com.savent.erp.domain.usecase

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.local.model.SaleEntity
import com.savent.erp.data.remote.model.Sale
import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class GetPendingSaleUseCase(private val salesRepository: SalesRepository) {

    operator fun invoke(): Flow<Resource<SaleEntity>> = flow {
        salesRepository.getPendingSale().onEach {
            if (it is Resource.Success)
                it.data?.let { it1 -> emit(Resource.Success(it1)) }
                    ?: emit(Resource.Error<SaleEntity>(resId = R.string.get_sales_error))
            else emit(Resource.Error<SaleEntity>(resId = R.string.get_sales_error))
        }.collect()

    }
}