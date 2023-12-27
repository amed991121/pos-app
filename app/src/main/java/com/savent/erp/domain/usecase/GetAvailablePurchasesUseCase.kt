package com.savent.erp.domain.usecase

import android.util.Log
import com.savent.erp.R
import com.savent.erp.domain.repository.EmployeesRepository
import com.savent.erp.domain.repository.ProvidersRepository
import com.savent.erp.domain.repository.PurchasesRepository
import com.savent.erp.presentation.ui.model.PurchaseItem
import com.savent.erp.utils.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import java.util.Locale.filter


class GetAvailablePurchasesUseCase(
    private val purchasesRepository: PurchasesRepository,
    private val employeesRepository: EmployeesRepository,
    private val providersRepository: ProvidersRepository,
) {

    operator fun invoke(query: String): Flow<Resource<List<PurchaseItem>>> = flow {

        purchasesRepository.getPurchases().onEach { resource ->
            if (resource is Resource.Error || resource.data == null) {
                emit(Resource.Error(resource.resId, resource.message))
                return@onEach
            }
            val purchases =
                resource.data.map { purchase ->
                    val employee =
                        employeesRepository.getEmployee(purchase.buyerId).data ?: kotlin.run {
                            emit(Resource.Error(resId = R.string.get_employees_error))
                            return@onEach
                        }
                    val provider = providersRepository.getProvider(purchase.providerId).data
                        ?: kotlin.run {
                            emit(Resource.Error(resId = R.string.get_providers_error))
                            return@onEach
                        }

                    PurchaseItem(
                        purchase.id,
                        Mappers.Ui.employee(employee).name,
                        Mappers.Ui.provider(provider).name,
                        DecimalFormat.format(purchase.amount),
                        purchase.remainingUnits,
                        purchase.dateTimestamp.date,
                        DateFormat.format(purchase.dateTimestamp.toLong(), "hh:mm a")
                    )

                }.filter { item ->
                    item.remainingUnits > 0 && (item.buyer.contains(
                        query,
                        true
                    ) || item.provider.contains(query, true))
                }

            emit(Resource.Success(purchases))

        }.collect()
    }
}