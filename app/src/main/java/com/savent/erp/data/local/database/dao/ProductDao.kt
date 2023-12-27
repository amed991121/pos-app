package com.savent.erp.data.local.database.dao

import androidx.room.*
import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.utils.PendingRemoteAction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProduct(product: ProductEntity):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>):List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProduct(product: ProductEntity):Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProducts(product: List<ProductEntity>):Int

    @Query("DELETE FROM products WHERE id =:id")
    suspend fun delete(id: Int):Int

    @Query("DELETE FROM products")
    suspend fun deleteAll():Int

    @Query("UPDATE products SET remote_action =:deleteAction WHERE id =:id")
    suspend fun toPendingDelete(id: Int, deleteAction: PendingRemoteAction = PendingRemoteAction.DELETE):Int

    @Query("SELECT * FROM products WHERE id =:id")
    suspend fun getProduct(id: Int): ProductEntity?

    @Query("SELECT * FROM products WHERE remote_id =:remoteId")
    suspend fun getProduct(remoteId: Long): ProductEntity?

    @Query("SELECT * FROM products")
    fun getProducts(): Flow<List<ProductEntity>?>


    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%'  ORDER BY name ASC")
    fun getProducts(query: String): Flow<List<ProductEntity>?>
}