package com.savent.erp.presentation.ui.model

data class ClientError(
    var nameError: Int? = null,
    var paternalError: Int? = null,
    var maternalError: Int? = null,
    var socialReasonError: Int? = null,
    var rfcError: Int? = null,
    var phoneError:Int? = null,
    var emailError:Int? = null,
    var streetError: Int? = null,
    var noExteriorError:Int? = null,
    var coloniaError: Int? = null,
    var postalCodeError: Int? = null,
    var cityError: Int? = null,
    var stateError: Int? = null,
    var countryError: Int? = null,
)