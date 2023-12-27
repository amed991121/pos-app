package com.savent.erp.data.remote.datasource

import com.savent.erp.data.common.model.Employee
import com.savent.erp.utils.Resource

interface EmployeesRemoteDataSource {
    suspend fun getEmployees(
        companyId: Int
    ): Resource<List<Employee>>
}