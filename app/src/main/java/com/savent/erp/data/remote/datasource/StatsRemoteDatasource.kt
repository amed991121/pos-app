package com.savent.erp.data.remote.datasource

import com.savent.erp.data.remote.model.Stat
import com.savent.erp.utils.Resource

interface StatsRemoteDatasource {

    fun getStat(businessId: Int, id: Int): Resource<Stat>

    fun getStats(businessId: Int): Resource<List<Stat>>

}