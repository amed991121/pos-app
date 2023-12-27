package com.savent.erp.data.repository

import com.savent.erp.data.common.model.Employee
import com.savent.erp.data.local.datasource.EmployeesLocalDatasource
import com.savent.erp.data.remote.datasource.EmployeesRemoteDataSource
import com.savent.erp.domain.repository.EmployeesRepository
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class EmployeesRepositoryImpl(
    private val localDatasource: EmployeesLocalDatasource,
    private val remoteDataSource: EmployeesRemoteDataSource
) : EmployeesRepository {
    override suspend fun insertEmployees(employees: List<Employee>): Resource<Int> =
        localDatasource.insertEmployees(employees)

    override suspend fun getEmployee(id: Int): Resource<Employee> =
        localDatasource.getEmployee(id)

    override fun getEmployees(query: String): Flow<Resource<List<Employee>>> = flow {
        localDatasource.getEmployees(query).onEach { emit(it) }.collect()
    }

    override suspend fun fetchEmployees(companyId: Int): Resource<Int> {
        val response = remoteDataSource.getEmployees(companyId)
        if (response is Resource.Error || response.data == null) return Resource.Error(
            resId = response.resId,
            message = response.message
        )
        return localDatasource.insertEmployees(response.data)
    }
}