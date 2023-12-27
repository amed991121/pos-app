package com.savent.erp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.data.local.model.MovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(movement: MovementEntity):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovements(movements: List<MovementEntity>):List<Long>

    @Query("SELECT * FROM movements WHERE id =:id")
    suspend fun getMovement(id:Int): MovementEntity?

    @Query("SELECT * FROM movements WHERE remote_id =:remoteId")
    suspend fun getMovementByRemoteId(remoteId:Int): MovementEntity?

    @Query("SELECT * FROM movements WHERE type IN(:type) ORDER BY remote_id DESC ")
    fun getMovements(type: List<MovementType>): Flow<List<MovementEntity>?>

    @Query("SELECT * FROM movements")
    fun getMovements(): List<MovementEntity>

    @Update
    fun updateMovement(movement: MovementEntity):Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMovements(movements: List<MovementEntity>):Int

    @Query("DELETE FROM movements")
    suspend fun deleteAll(): Int

}