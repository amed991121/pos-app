package com.savent.erp.data.remote.datasource

import com.savent.erp.data.remote.model.Stat
import com.savent.erp.data.remote.service.StatApiService
import com.savent.erp.utils.Resource

class StatsRemoteDatasourceImpl(private val statsService: StatApiService) : StatsRemoteDatasource{

    override fun getStat(businessId: Int, id: Int): Resource<Stat> {
        TODO("Not yet implemented")
    }

    override fun getStats(businessId: Int): Resource<List<Stat>> {
        TODO("Not yet implemented")
    }
}