package com.savent.erp.data.local.database.dao

import androidx.room.*
import com.savent.erp.data.local.model.DebtPaymentEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface DebtPaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebtPayments(debtPayments: List<DebtPaymentEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebtPayment(debtPayment: DebtPaymentEntity): Long

    @Query("SELECT * FROM debt_payments ORDER BY remote_id DESC")
    fun getDebtPayments(): Flow<List<DebtPaymentEntity>?>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDebtPayment(debtPayment: DebtPaymentEntity): Int

}