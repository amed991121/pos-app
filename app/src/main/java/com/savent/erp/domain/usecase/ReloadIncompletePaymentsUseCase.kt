package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.IncompletePaymentRepository
import com.savent.erp.utils.Resource

class ReloadIncompletePaymentsUseCase(
    private val incompletePaymentRepository: IncompletePaymentRepository
) {
    suspend operator fun invoke(businessId: Int, storeId: Int, companyId: Int): Resource<Int> =
        incompletePaymentRepository.fetchIncompletePayments(businessId, storeId, companyId)
}