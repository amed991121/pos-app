package com.savent.erp.domain.usecase

import android.util.Log
import com.savent.erp.R
import com.savent.erp.data.remote.model.Movement
import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.domain.repository.PurchasesRepository
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.first

class AttachPurchaseToMovementUseCase(
    private val purchasesRepository: PurchasesRepository,
    private val businessBasicsRepository: BusinessBasicsRepository
) {

    suspend operator fun invoke(movement: Movement): Resource<Movement> {

        val businessBasics =
            businessBasicsRepository.getBusinessBasics().first().data
                ?: return Resource.Error(resId = R.string.get_business_error)

        purchasesRepository.fetchPurchases(businessBasics.companyId)

        val purchase = movement.purchaseId?.let {
            purchasesRepository.getPurchase(it).data
                ?: return Resource.Error(resId = R.string.purchase_not_available)
        }
            ?: return Resource.Success(movement)

        val remainingProducts = purchase.remainingProducts
        var remainingUnits = purchase.remainingUnits
        for ((productId, units) in movement.selectedProducts) {
            val remainingVal = remainingProducts[productId] ?: 0
            remainingProducts[productId] = remainingVal - units
            remainingUnits -= units
        }
        purchasesRepository.updatePurchase(
            purchase.copy(
                remainingProducts = remainingProducts,
                remainingUnits = remainingUnits
            )
        ).let { if (it is Resource.Error) return Resource.Error(resId = it.resId) }

        return Resource.Success(
            movement.copy(
                providerId = purchase.providerId
            )
        )
    }
}