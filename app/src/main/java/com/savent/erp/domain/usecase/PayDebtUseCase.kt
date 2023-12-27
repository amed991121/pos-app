package com.savent.erp.domain.usecase

import android.util.Log
import com.savent.erp.R
import com.savent.erp.data.remote.model.DebtPayment
import com.savent.erp.domain.repository.DebtPaymentRepository
import com.savent.erp.domain.repository.IncompletePaymentRepository
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource


class PayDebtUseCase(
    private val debtPaymentRepository: DebtPaymentRepository,
    private val incompletePaymentRepository: IncompletePaymentRepository
) {

    suspend operator fun invoke(debtPayment: DebtPayment, saleId: Int): Resource<Int> {
        debtPaymentRepository.insertDebtPayment(debtPayment).let {
            if (it is Resource.Error) return it
            val incompletePayment =
                incompletePaymentRepository.getIncompletePayment(saleId)
            if (incompletePayment is Resource.Success && incompletePayment.data != null)
                incompletePaymentRepository.updateIncompletePayment(
                    incompletePayment.data.copy(
                        collected = incompletePayment.data.collected + debtPayment.paid,
                        pendingRemoteAction = PendingRemoteAction.INSERT
                    )
                )
            else Resource.Error(resId = R.string.insert_debt_payments_error)
        }
        return Resource.Success()
    }

}