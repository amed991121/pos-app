package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.EmployeesRepository
import com.savent.erp.utils.Resource

class ReloadEmployeesUseCase(private val employeesRepository: EmployeesRepository) {

    suspend operator fun invoke(companyId: Int): Resource<Int> =
        employeesRepository.fetchEmployees(companyId)

}