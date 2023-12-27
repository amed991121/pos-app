package com.savent.erp.data.repository

import com.savent.erp.data.local.datasource.StatsLocalDatasource
import com.savent.erp.data.local.model.ProductEntity
import com.savent.erp.data.local.model.StatEntity
import com.savent.erp.data.remote.datasource.ProductsRemoteDatasource
import com.savent.erp.data.remote.model.Product
import com.savent.erp.data.remote.model.Stat
import com.savent.erp.domain.repository.StatsRepository
import com.savent.erp.utils.PendingRemoteAction
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StatsRepositoryImpl(
    private val localDatasource: StatsLocalDatasource,
    private val remoteDatasource: ProductsRemoteDatasource
) : StatsRepository {

    override suspend fun insertStats(stats: List<Stat>): Resource<Int> {
        return Resource.Success()
    }

    override fun getStat(id: Int): Flow<Resource<StatEntity>> = flow{
        emit( Resource.Success())
    }

    override fun getStats(): Flow<Resource<List<StatEntity>>> = flow{
        emit( Resource.Success())
    }

    override suspend fun fetchStats(businessId: Int): Resource<Int> {
        return Resource.Success()
    }

    /*private fun mapToEntity(stat: Stat): ProductEntity {
        return StatEntity(

        )
    }*/

}