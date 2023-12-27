package com.savent.erp.data.remote.datasource

import com.savent.erp.data.remote.model.Company
import com.savent.erp.utils.Resource

interface CompaniesRemoteDatasource {
    suspend fun getCompanies(): Resource<List<Company>>
}