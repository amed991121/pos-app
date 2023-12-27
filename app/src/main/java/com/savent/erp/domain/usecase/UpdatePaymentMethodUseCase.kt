package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.utils.PaymentMethod
import com.savent.erp.utils.Resource

class UpdatePaymentMethodUseCase (
    private val salesRepository: SalesRepository

) {
    suspend operator fun invoke(method: PaymentMethod): Resource<Int> =
        salesRepository.updatePaymentMethod(method)


}