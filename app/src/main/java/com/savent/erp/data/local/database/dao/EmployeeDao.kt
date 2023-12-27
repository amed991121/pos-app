package com.savent.erp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.savent.erp.data.common.model.Employee
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<Employee>):List<Long>

    @Query("SELECT * FROM employees WHERE id =:id")
    suspend fun getEmployee(id:Int): Employee?

    @Query("SELECT * FROM employees ORDER BY name ASC")
    suspend fun getEmployees(): List<Employee>?

    @Query("SELECT * FROM employees WHERE name LIKE '%' || :query || '%' OR paternal_name LIKE '%' || :query || '%' OR maternal_name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun getEmployees(query: String): Flow<List<Employee>?>

    @Query("DELETE FROM employees")
    suspend fun deleteAll(): Int

}