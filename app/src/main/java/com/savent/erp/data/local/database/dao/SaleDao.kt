package com.savent.erp.data.local.database.dao

import androidx.room.*
import com.savent.erp.data.local.model.SaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleEntity):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSales(sales: List<SaleEntity>):List<Long>

    @Query("SELECT * FROM sales WHERE id =:id")
    suspend fun getSale(id:Int): SaleEntity?

    @Query("SELECT * FROM sales WHERE remote_id =:remoteId")
    suspend fun getSaleByRemoteId(remoteId:Int): SaleEntity?

    @Query("SELECT * FROM sales ORDER BY remote_id DESC")
    fun getSales(): Flow<List<SaleEntity>?>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateSale(sale: SaleEntity):Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSales(sales: List<SaleEntity>):Int

}