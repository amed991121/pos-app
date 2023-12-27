package com.savent.erp.domain.usecase

import android.util.Log
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.local.model.BusinessBasicsLocal
import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.presentation.ui.model.BusinessBasicsItem
import com.savent.erp.utils.NameFormat
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class GetBusinessBasicsUseCase(private val businessBasicsRepository: BusinessBasicsRepository) {

    operator fun invoke(): Flow<Resource<BusinessBasicsItem>> = flow {
        businessBasicsRepository.getBusinessBasics().onEach {
            val result: Resource<BusinessBasicsItem> = if (it is Resource.Success) {
                Resource.Success(it.data?.let { it1-> mapToUiItem(it1) })
            } else {
                Resource.Error(resId = R.string.get_business_error)
            }
            emit(result)
        }.collect()
    }

    private fun mapToUiItem(businessBasics: BusinessBasicsLocal): BusinessBasicsItem {
        return BusinessBasicsItem(
            businessBasics.id,
            NameFormat.format(businessBasics.name),
            businessBasics.sellerLevel,
            NameFormat.format(businessBasics.storeName),
            businessBasics.image,
            NameFormat.format(businessBasics.address),
            businessBasics.receiptFooter
        )
    }

}