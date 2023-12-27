package com.savent.erp.data.local.datasource

import android.util.Log
import com.savent.erp.R
import com.savent.erp.data.common.model.Employee
import com.savent.erp.data.local.database.dao.EmployeeDao
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class EmployeesLocalDatasourceImpl(private val employeeDao: EmployeeDao) :
    EmployeesLocalDatasource {
    override suspend fun insertEmployees(employees: List<Employee>): Resource<Int> {
        employeeDao.deleteAll()
        val result = employeeDao.insertEmployees(employees)
        if (result.size != employees.size)
            return Resource.Error(resId = R.string.insert_employees_error)
        return Resource.Success()
    }

    override suspend fun getEmployee(id: Int): Resource<Employee> =
        Resource.Success(employeeDao.getEmployee(id))

    override fun getEmployees(query: String): Flow<Resource<List<Employee>>> = flow {
        employeeDao.getEmployees(query).onEach {
            it?.let { it1 -> emit(Resource.Success(it1)) }
                ?: emit(Resource.Error(resId = R.string.get_employees_error))
        }.collect()
    }
}