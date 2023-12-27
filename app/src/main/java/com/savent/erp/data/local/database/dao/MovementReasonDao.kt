package com.savent.erp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.savent.erp.data.common.model.MovementReason
import com.savent.erp.data.common.model.MovementType
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementReasonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReasons(reasons: List<MovementReason>): List<Long>

    @Query("SELECT * FROM movement_reasons WHERE id =:id")
    suspend fun getReason(id: Int): MovementReason?

    @Query("SELECT * FROM movement_reasons WHERE name LIKE '%' || :query || '%'  AND type=:typeFilter ORDER BY name ASC ")
    fun getReasons(query: String, typeFilter: MovementType): Flow<List<MovementReason>?>

    @Query("DELETE FROM movement_reasons")
    suspend fun deleteAll(): Int
}