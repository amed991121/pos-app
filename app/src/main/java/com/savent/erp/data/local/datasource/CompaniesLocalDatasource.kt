package com.savent.erp.data.local.datasource

import com.savent.erp.data.local.model.CompanyEntity
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

abstract class CompaniesLocalDatasource {

    abstract suspend fun insertCompanies(vararg companies: CompanyEntity): Resource<Int>

    abstract suspend fun updateCompanies(vararg companies: CompanyEntity): Resource<Int>

    abstract suspend fun getCompany(id: Int): Resource<CompanyEntity>

    abstract suspend fun getCompanyByRemoteId(remoteId: Int): Resource<CompanyEntity>

    abstract suspend fun getCompanies(): Resource<List<CompanyEntity>>

    abstract fun getCompanies(query: String): Flow<Resource<List<CompanyEntity>>>

    protected fun alreadyExists(company: CompanyEntity, oldData: List<CompanyEntity>): Boolean {
        oldData.forEach {
            if (company.remoteId == it.remoteId) return true
        }
        return false
    }

    protected fun areContentsTheSame(company: CompanyEntity, oldData: List<CompanyEntity>): Boolean {
        oldData.forEach {
            if (company.hashCode() == it.hashCode()) return true
        }
        return false
    }

    protected fun prepareDataToUpdate(
        newData: List<CompanyEntity>,
        oldData: List<CompanyEntity>
    ): List<CompanyEntity> {
        newData.forEach { newCompany ->
            oldData.forEach { oldCompany ->
                if (newCompany.remoteId == oldCompany.remoteId)
                    newCompany.id = oldCompany.id
            }
        }
        return newData
    }
}