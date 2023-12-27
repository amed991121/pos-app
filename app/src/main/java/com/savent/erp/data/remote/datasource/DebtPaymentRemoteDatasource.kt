package com.savent.erp.data.remote.datasource

import com.savent.erp.data.remote.model.DebtPayment
import com.savent.erp.utils.Resource

interface DebtPaymentRemoteDatasource {
    suspend fun insertDebtPayment(
        businessId: Int,
        sellerId: Int,
        storeId: Int,
        debtPayment: DebtPayment,
        companyId: Int,
    ): Resource<Int>

    suspend fun getDebtPayments(storeId: Int, companyId: Int): Resource<List<DebtPayment>>
}