package com.savent.erp.data.local.datasource

import com.savent.erp.data.local.database.dao.StatDao
import com.savent.erp.data.local.model.StatEntity
import com.savent.erp.data.remote.model.Stat
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

class StatsLocalDataSourceImpl(private val statsDao: StatDao): StatsLocalDatasource {

    override suspend fun insertStats(stats: List<Stat>): Resource<Int> {
        TODO("Not yet implemented")
    }

    override fun getStat(id: Int): Flow<Resource<StatEntity>> {
        TODO("Not yet implemented")
    }

    override fun getStats(): Flow<Resource<List<StatEntity>>> {
        TODO("Not yet implemented")
    }
}