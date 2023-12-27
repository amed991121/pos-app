package com.savent.erp.data.local.database.dao

import androidx.room.*
import com.savent.erp.data.common.model.Purchase
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchases(purchases: List<Purchase>):List<Long>

    @Query("SELECT * FROM purchases WHERE id =:id")
    suspend fun getPurchase(id:Int): Purchase?

    @Update
    suspend fun updatePurchase(purchase: Purchase): Int

    @Query("SELECT * FROM purchases ORDER BY id DESC")
    fun getPurchases(): Flow<List<Purchase>?>

    @Query("DELETE FROM purchases")
    suspend fun deleteAll(): Int
}