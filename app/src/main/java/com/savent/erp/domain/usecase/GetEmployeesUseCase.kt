package com.savent.erp.domain.usecase

import com.savent.erp.data.common.model.Employee
import com.savent.erp.domain.repository.EmployeesRepository
import com.savent.erp.presentation.ui.model.EmployeeItem
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*


class GetEmployeesUseCase(
    private val employeesRepository: EmployeesRepository,
    private val employeeUiMapper: (Employee) -> EmployeeItem
) {

    operator fun invoke(query: String): Flow<Resource<List<EmployeeItem>>> = flow {
        employeesRepository.getEmployees(query).onEach { result ->
            if (result is Resource.Error || result.data == null) {
                emit(
                    Resource.Error(
                        result.resId,
                        result.message
                    )
                )
                return@onEach
            }
            emit(Resource.Success(result.data.map(employeeUiMapper)))
        }.collect()
    }
}