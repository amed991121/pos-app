package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.utils.Resource

class ReloadSalesUseCase(private val salesRepository: SalesRepository) {

    suspend operator fun invoke(
        businessId: Int,
        storeId: Int,
        date: String,
        companyId: Int,
    ): Resource<Int> =
        salesRepository.fetchSales(businessId, storeId, date, companyId)

}