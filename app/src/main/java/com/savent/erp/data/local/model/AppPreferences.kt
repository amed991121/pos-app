package com.savent.erp.data.local.model

data class AppPreferences(
    var clientsFilter: String,
    var productsFilter: String,
    var loadProductsDiscounts: Boolean
)