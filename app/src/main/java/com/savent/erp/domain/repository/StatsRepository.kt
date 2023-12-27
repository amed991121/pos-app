package com.savent.erp.domain.repository

import com.savent.erp.data.local.model.StatEntity
import com.savent.erp.utils.Resource
import com.savent.erp.data.remote.model.Stat
import kotlinx.coroutines.flow.Flow

interface StatsRepository {

    suspend fun insertStats(stats: List<Stat>): Resource<Int>

    fun getStat(id: Int): Flow<Resource<StatEntity>>

    fun getStats(): Flow<Resource<List<StatEntity>>>

    suspend fun fetchStats(businessId: Int): Resource<Int>

}