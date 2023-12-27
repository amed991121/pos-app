package com.savent.erp.domain.repository

import com.savent.erp.data.local.model.CompanyEntity
import com.savent.erp.data.remote.model.Company
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CompaniesRepository {

    suspend fun insertCompanies(vararg companies: Company): Resource<Int>

    suspend fun getCompany(id: Int): Resource<CompanyEntity>

    suspend fun getCompanyByRemoteId(remoteId: Int): Resource<CompanyEntity>

    suspend fun getCompanies(): Resource<List<CompanyEntity>>

    fun getCompanies(query: String): Flow<Resource<List<CompanyEntity>>>

    suspend fun fetchCompanies(): Resource<Int>

}