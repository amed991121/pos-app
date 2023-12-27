package com.savent.erp.presentation.ui.model

data class ClientItem(
    val localId: Int,
    val name: String,
    val image: String?,
    val address: String,
    val isSelected: Boolean = false
)
