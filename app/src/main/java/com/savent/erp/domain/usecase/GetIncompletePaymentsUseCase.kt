package com.savent.erp.domain.usecase

import com.savent.erp.R
import com.savent.erp.data.local.model.IncompletePaymentEntity
import com.savent.erp.domain.repository.IncompletePaymentRepository
import com.savent.erp.presentation.ui.model.IncompletePaymentItem
import com.savent.erp.utils.DateFormat
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class GetIncompletePaymentsUseCase(
    private val incompletePaymentRepository: IncompletePaymentRepository
) {
    suspend operator fun invoke(clientId: Int): Flow<Resource<List<IncompletePaymentItem>>> = flow {
        incompletePaymentRepository.getIncompletePayments(clientId).onEach {
            if (it is Resource.Error || it.data == null) {
                emit(
                    Resource.Error(
                        resId = R.string.get_incomplete_payments_error
                    )
                )
                return@onEach
            }
            val incompletePayments: List<IncompletePaymentItem> = it.data.filter { entity ->
               entity.collected < entity.total
            }.map { it1 -> mapToUiItem(it1) }

            emit(Resource.Success(incompletePayments))

        }.collect()

    }

    private fun mapToUiItem(entity: IncompletePaymentEntity): IncompletePaymentItem {
        val time = DateFormat.format(entity.dateTimestamp, "yyyy-MM-dd")
        return IncompletePaymentItem(
            entity.id,
            entity.saleId,
            time,
            entity.productsUnits,
            entity.subtotal,
            entity.IVA,
            entity.discounts,
            entity.total,
            entity.collected,
            entity.total - entity.collected
        )
    }
}