package com.savent.erp.presentation.ui.model

data class LoginError(
    val rfcError: Int? = null,
    val pinError: Int? = null,
    val storeError: Int? = null,
) {
}