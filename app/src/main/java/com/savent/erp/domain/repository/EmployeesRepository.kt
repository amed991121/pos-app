package com.savent.erp.domain.repository

import com.savent.erp.data.common.model.Employee
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface EmployeesRepository {

    suspend fun insertEmployees(employees: List<Employee>): Resource<Int>

    suspend fun getEmployee(id: Int): Resource<Employee>

    fun getEmployees(query: String): Flow<Resource<List<Employee>>>

    suspend fun fetchEmployees(companyId: Int): Resource<Int>


}