package com.savent.erp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.savent.erp.data.common.model.Discount

@Dao
interface DiscountsDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertDiscounts(discounts: List<Discount>): List<Long>

    @Query("SELECT * FROM discounts WHERE product_id =:productId AND client_id =:clientId")
    suspend fun getDiscount(productId: Int, clientId: Int): Discount?

    @Query("DELETE FROM discounts WHERE client_id =:clientId")
    suspend fun deleteDiscount(clientId: Int): Int

}