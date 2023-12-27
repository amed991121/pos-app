package com.savent.erp.data.remote.model

import com.google.gson.annotations.SerializedName
import com.savent.erp.data.common.model.*

data class Business(
    val basics: BusinessBasics,
    val clients: List<Client>,
    val products: List<Product>,
    val discounts: List<Discount>,
    val sales: List<Sale>,
    @SerializedName("incomplete_payments")
    val incompletePayments: List<IncompletePayment>,
    @SerializedName("debt_payments")
    val debtPayments: List<DebtPayment>,
    val employees: List<Employee>,
    val providers: List<Provider>,
    @SerializedName("movement_reasons")
    val movementReasons: List<MovementReason>,
    val movements: List<Movement>,
    val purchases: List<Purchase>
) {
}