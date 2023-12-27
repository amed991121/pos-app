package com.savent.erp.domain.usecase

import com.savent.erp.data.common.model.MovementReason
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.domain.repository.MovementReasonsRepository
import com.savent.erp.presentation.ui.model.MovementReasonItem
import com.savent.erp.presentation.viewmodel.MovementsViewModel
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*

class GetMovementReasonsUseCase(
    private val movementsReasonsRepository: MovementReasonsRepository,
    private val mapper: (MovementReason) -> (MovementReasonItem)
) {
    operator fun invoke(
        query: String,
        type: MovementType
    ): Flow<Resource<List<MovementReasonItem>>> = flow {
        movementsReasonsRepository.getMovementsReasons(query, type).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { emit(Resource.Success(it.map(mapper)))}
                }
                is Resource.Error -> {
                    emit(Resource.Error(resource.resId, resource.message))
                }
                else -> {}
            }
        }.collect()

    }
}