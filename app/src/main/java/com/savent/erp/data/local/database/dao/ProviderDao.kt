package com.savent.erp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.savent.erp.data.common.model.Provider
import kotlinx.coroutines.flow.Flow

@Dao
interface ProviderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(providers: List<Provider>):List<Long>

    @Query("SELECT * FROM providers WHERE id =:id")
    suspend fun getProvider(id:Int): Provider?

    @Query("SELECT * FROM providers ORDER BY trade_name ASC")
    suspend fun getProviders(): List<Provider>?

    @Query("SELECT * FROM providers WHERE trade_name LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' OR paternal_name LIKE '%' || :query || '%' OR maternal_name LIKE '%' || :query || '%' ORDER BY trade_name ASC")
    fun getProviders(query: String): Flow<List<Provider>?>

    @Query("DELETE FROM providers")
    suspend fun deleteAll(): Int

}