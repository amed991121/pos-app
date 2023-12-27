package com.savent.erp.data.local.database.dao

import androidx.room.*
import com.savent.erp.data.local.model.ClientEntity
import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.data.remote.model.Client
import com.savent.erp.utils.PendingRemoteAction
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addClient(client: ClientEntity):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientEntity>):List<Long>

    @Update
    suspend fun updateClient(client: ClientEntity):Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateClients(clients: List<ClientEntity>):Int

    @Query("DELETE FROM clients WHERE id =:id")
    suspend fun deleteClient(id: Int):Int

    @Query("UPDATE products SET remote_action =:deleteAction WHERE id =:id")
    suspend fun toPendingDelete(id: Int, deleteAction: PendingRemoteAction = PendingRemoteAction.DELETE):Int

    @Query("SELECT * FROM clients WHERE id =:id")
    fun getClient(id: Int): ClientEntity?

    @Query("SELECT * FROM clients WHERE remote_id =:remoteId")
    fun getClientByRemoteId(remoteId: Int): ClientEntity?

    @Query("SELECT * FROM clients")
    fun getClients(): Flow<List<ClientEntity>?>

    @Query("SELECT * FROM clients WHERE name LIKE '%' || :query || '%' OR trade_name LIKE '%' || :query || '%' OR paternal LIKE '%' || :query || '%' OR maternal LIKE '%' || :query || '%' ORDER BY name ASC")
    fun getClients(query: String): Flow<List<ClientEntity>?>

    @Query("DELETE FROM clients WHERE id =:id")
    suspend fun delete(id: Int):Int

    @Query("DELETE FROM clients")
    suspend fun deleteAll():Int


}