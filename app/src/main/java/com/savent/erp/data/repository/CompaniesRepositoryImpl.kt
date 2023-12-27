package com.savent.erp.data.repository

import com.savent.erp.data.local.datasource.CompaniesLocalDatasource
import com.savent.erp.data.local.model.CompanyEntity
import com.savent.erp.data.remote.datasource.CompaniesRemoteDatasource
import com.savent.erp.data.remote.model.Company
import com.savent.erp.domain.repository.CompaniesRepository
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class CompaniesRepositoryImpl(
    private val localDataSource: CompaniesLocalDatasource,
    private val remoteDatasource: CompaniesRemoteDatasource,
    private val mapper: (Company) -> CompanyEntity
) : CompaniesRepository {
    override suspend fun insertCompanies(vararg companies: Company): Resource<Int> =
        localDataSource.insertCompanies(*(companies.map { mapper(it) }.toTypedArray()))

    override suspend fun getCompany(id: Int): Resource<CompanyEntity> =
        localDataSource.getCompany(id)

    override suspend fun getCompanyByRemoteId(remoteId: Int): Resource<CompanyEntity> =
        localDataSource.getCompanyByRemoteId(remoteId)

    override suspend fun getCompanies(): Resource<List<CompanyEntity>> =
        localDataSource.getCompanies()

    override fun getCompanies(query: String): Flow<Resource<List<CompanyEntity>>> = flow{
        localDataSource.getCompanies(query).onEach { emit(it) }.collect()
    }

    override suspend fun fetchCompanies(): Resource<Int> {
        val response = remoteDatasource.getCompanies()
        if (response is Resource.Success) {
            response.data?.let {
                //Log.d("log_","response"+Gson().toJson(it))
                insertCompanies(*(it.toTypedArray()))
            }
            return Resource.Success()
        }
        return Resource.Error(resId = response.resId, message = response.message)
    }
}