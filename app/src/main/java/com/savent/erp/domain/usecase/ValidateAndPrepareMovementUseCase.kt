package com.savent.erp.domain.usecase

import com.savent.erp.R
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.data.remote.model.Movement
import com.savent.erp.domain.repository.MovementReasonsRepository
import com.savent.erp.utils.Resource

class ValidateAndPrepareMovementUseCase(
    private val movementReasonsRepository: MovementReasonsRepository,
) {
    suspend operator fun invoke(movement: Movement): Resource<Movement> {
        if (movement.reasonId == 0) return Resource.Error(R.string.movement_reason_required)
        val reason = movementReasonsRepository.getReason(movement.reasonId).data
            ?: return Resource.Error(R.string.get_movements_error)
        if (reason.name.contains("Compra", true)) {
            if (movement.purchaseId == null) return Resource.Error(R.string.purchase_required)
        }
        var finalMovement: Movement = movement
        when (movement.type) {
            MovementType.INPUT -> {
                if (movement.inputStoreKeeperId == null)
                    return Resource.Error(R.string.store_keeper_required)
                if (movement.inputStoreId == 0)
                    return Resource.Error(R.string.store_required)
                finalMovement = movement.copy(outputStoreKeeperId = null, outputStoreId = 0)
            }
            MovementType.OUTPUT -> {
                if (movement.outputStoreKeeperId == null)
                    return Resource.Error(R.string.store_keeper_required)
                if (movement.outputStoreId == 0)
                    return Resource.Error(R.string.store_required)
                finalMovement = movement.copy(inputStoreKeeperId = null, inputStoreId = 0)
            }
            else -> {
                if (movement.inputStoreKeeperId == null)
                    return Resource.Error(R.string.store_keeper_required)
                if (movement.inputStoreId == 0)
                    return Resource.Error(R.string.store_required)
                if (movement.outputStoreKeeperId == null)
                    return Resource.Error(R.string.store_keeper_required)
                if (movement.outputStoreId == 0)
                    return Resource.Error(R.string.store_required)
            }
        }

        if (movement.units == 0) return Resource.Error(R.string.one_product_at_least)

        return Resource.Success(finalMovement)
    }
}