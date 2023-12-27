package com.savent.erp.data.remote.datasource

import com.savent.erp.data.remote.model.IncompletePayment
import com.savent.erp.utils.Resource

interface IncompletePaymentsRemoteDatasource {

    suspend fun getIncompletePayments(businessId: Int, storeId: Int, companyId: Int):
            Resource<List<IncompletePayment>>

}