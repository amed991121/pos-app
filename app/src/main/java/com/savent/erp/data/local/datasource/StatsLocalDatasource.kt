package com.savent.erp.data.local.datasource

import com.savent.erp.data.local.model.StatEntity
import com.savent.erp.utils.Resource
import com.savent.erp.data.remote.model.Stat
import kotlinx.coroutines.flow.Flow

interface StatsLocalDatasource {

    suspend fun insertStats(stats: List<Stat>): Resource<Int>

    fun getStat(id: Int): Flow<Resource<StatEntity>>

    fun getStats(): Flow<Resource<List<StatEntity>>>

}