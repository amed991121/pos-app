package com.savent.erp.domain.usecase

import android.util.Log
import com.savent.erp.R
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.domain.repository.*
import com.savent.erp.presentation.ui.model.MovementItem
import com.savent.erp.utils.*
import kotlinx.coroutines.flow.*

class GetMovementsOfDayUseCase(
    private val movementsRepository: MovementsRepository,
    private val reasonsRepository: MovementReasonsRepository,
    private val storesRepository: StoresRepository,
    private val employeesRepository: EmployeesRepository,
    private val businessBasicsRepository: BusinessBasicsRepository,
) {
    operator fun invoke(
        groupType: MovementType,
        query: String
    ): Flow<Resource<List<MovementItem>>> = flow {

        movementsRepository.getMovements(groupType).onEach {
            if (it is Resource.Error || it.data == null) {
                emit(Resource.Error(resId = R.string.get_movements_error))
                return@onEach
            }

            val movements: List<MovementItem> = it.data.filter { movementEntity ->
                IsFromToday(movementEntity.dateTimestamp)
            }.map { entity ->

                val basics =
                    businessBasicsRepository.getBusinessBasics().first().data ?: kotlin.run {
                        emit(Resource.Error(resId = R.string.get_business_error))
                        return@onEach
                    }
                val reason =
                    reasonsRepository.getReason(entity.reasonId).data ?: kotlin.run {
                        emit(Resource.Error(resId = R.string.get_reasons_error))
                        return@onEach
                    }
                val inputStore =
                    storesRepository.getStoreByRemoteId(entity.storeInputId, basics.companyId)
                        .let { resource ->
                            if (resource.data == null && entity.type != MovementType.OUTPUT) {
                                emit(Resource.Error(resId = R.string.get_stores_error))
                                return@onEach
                            }
                            resource.data
                        }
                val outputStore =
                    storesRepository.getStoreByRemoteId(entity.storeOutputId, basics.companyId)
                        .let { resource ->
                            if (resource.data == null && entity.type != MovementType.INPUT) {
                                emit(Resource.Error(resId = R.string.get_stores_error))
                                return@onEach
                            }
                            resource.data
                        }

                val employee = employeesRepository.getEmployee(entity.employeeId).data ?: run {
                    emit(Resource.Error(resId = R.string.get_employees_error))
                    return@onEach
                }

                val inputStoreKeeper =
                    entity.inputStoreKeeperId?.let { it1 ->
                        employeesRepository.getEmployee(it1).let { resource ->
                            if (resource.data == null && entity.type != MovementType.OUTPUT) {
                                emit(Resource.Error(resId = R.string.get_employees_error))
                                return@onEach
                            }
                            resource.data
                        }
                    }

                val outputStoreKeeper =
                    entity.outputStoreKeeperId?.let { it1 ->
                        employeesRepository.getEmployee(it1).let { resource ->
                            if (resource.data == null && entity.type != MovementType.INPUT) {
                                emit(Resource.Error(resId = R.string.get_employees_error))
                                return@onEach
                            }
                            resource.data
                        }
                    }

                MovementItem(
                    entity.id,
                    entity.remoteId,
                    entity.type,
                    NameFormat.format(reason.name),
                    DateFormat.format(entity.dateTimestamp, "yyyy-MM-dd"),
                    DateFormat.format(entity.dateTimestamp, "hh:mm a"),
                    NameFormat.format(employee.name),
                    entity.purchaseId,
                    inputStore?.name?.let { it1 -> NameFormat.format(it1) },
                    outputStore?.name?.let { it1 -> NameFormat.format(it1) },
                    inputStoreKeeper?.name?.let { it1 -> NameFormat.format(it1) },
                    outputStoreKeeper?.name?.let { it1 -> NameFormat.format(it1) },
                    entity.units,
                    entity.pendingRemoteAction == PendingRemoteAction.INSERT
                )

            }.filter { item ->
                item.employee.contains(query, true) || item.inputStore?.contains(
                    query,
                    true
                ) == true || item.outputStore?.contains(
                    query,
                    true
                ) == true || item.inputStoreKeeper?.contains(
                    query,
                    true
                ) == true || item.outputStoreKeeper?.contains(query, true) == true
            }

            emit(Resource.Success(movements))

        }.collect()
    }
}