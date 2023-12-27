package com.savent.erp.data.local.database.dao

import androidx.room.*
import com.savent.erp.data.local.model.IncompletePaymentEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface IncompletePaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncompletePayments(payments: List<IncompletePaymentEntity>): List<Long>

    @Query("SELECT * FROM incomplete_payments WHERE client_id =:clientId")
    fun getIncompletePayments(clientId: Int): Flow<List<IncompletePaymentEntity>?>

    @Query("SELECT * FROM incomplete_payments")
    fun getIncompletePayments(): Flow<List<IncompletePaymentEntity>?>

    @Query("SELECT * FROM incomplete_payments WHERE sale_id =:saleId")
    fun getIncompletePayment(saleId: Int): IncompletePaymentEntity?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateIncompletePayment(incompletePayment: IncompletePaymentEntity): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateIncompletePayments(incompletePayments: List<IncompletePaymentEntity>): Int

    @Query("DELETE FROM incomplete_payments WHERE id =:id")
    suspend fun delete(id: Int): Int

}