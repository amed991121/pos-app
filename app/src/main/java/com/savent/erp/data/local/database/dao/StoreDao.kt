package com.savent.erp.data.local.database.dao

import androidx.room.*
import com.savent.erp.data.local.model.StoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStores(vararg stores: StoreEntity):List<Long>

    @Update
    suspend fun updateStores(vararg stores: StoreEntity):Int

    @Query("SELECT * FROM stores WHERE id =:id AND company_id =:companyId")
    fun getStore(id: Int, companyId: Int): StoreEntity?

    @Query("SELECT * FROM stores WHERE remote_id =:remoteId AND company_id =:companyId")
    fun getStoreByRemoteId(remoteId: Int, companyId: Int): StoreEntity?

    @Query("SELECT * FROM stores WHERE company_id =:companyId")
    suspend fun getStores(companyId: Int): List<StoreEntity>?

    @Query("SELECT * FROM stores WHERE company_id =:companyId AND name LIKE '%' || :query || '%'  ORDER BY name ASC")
    fun getStores(query: String, companyId: Int): Flow<List<StoreEntity>?>

    @Query("DELETE FROM stores WHERE id IN(:ids)")
    suspend fun delete(vararg ids: Int):Int

    @Query("DELETE FROM stores ")
    suspend fun deleteAll():Int

}