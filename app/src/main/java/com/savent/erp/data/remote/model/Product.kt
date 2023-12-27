package com.savent.erp.data.remote.model

class Product(
    val id: Int,
    val name: String,
    val barcode: String?,
    val description: String?,
    val image: String?,
    val price: Float,
    val IEPS: Float?,
    val IVA: Float?,
    val units: Int?,
) {
}