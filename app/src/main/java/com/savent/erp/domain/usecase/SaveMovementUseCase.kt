package com.savent.erp.domain.usecase

import android.util.Log
import com.savent.erp.R
import com.savent.erp.data.remote.model.Movement
import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.domain.repository.MovementsRepository
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.first

class SaveMovementUseCase(
    private val movementsRepository: MovementsRepository,
    private val businessBasicsRepository: BusinessBasicsRepository,
    private val validateAndPrepareMovementUseCase: ValidateAndPrepareMovementUseCase,
    private val attachPurchaseToMovementUseCase: AttachPurchaseToMovementUseCase
) {
    suspend operator fun invoke(movement: Movement): Resource<Int> {
        val businessBasics =
            businessBasicsRepository.getBusinessBasics().first().data
                ?: return Resource.Error(resId = R.string.get_business_error)

        val movementValidated = validateAndPrepareMovementUseCase(movement).let { resource ->
            if (resource is Resource.Error) return Resource.Error(
                resource.resId
            )
            else resource.data ?: return Resource.Error()
        }

        var finalMovement = attachPurchaseToMovementUseCase(movementValidated).let { resource ->
            if (resource is Resource.Error) return Resource.Error(
                resource.resId
            )
            else resource.data ?: return Resource.Error()
        }

        finalMovement = finalMovement.copy(employeeId = businessBasics.sellerId)

        return movementsRepository.registerMovement(
            businessBasics.id,
            finalMovement,
            businessBasics.companyId
        )

    }
}