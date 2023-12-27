package com.savent.erp.domain.usecase

import android.util.Log
import android.util.Patterns
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.remote.model.Client
import com.savent.erp.presentation.ui.model.ClientError
import com.savent.erp.utils.Resource

class ValidateClientUseCase {

    operator fun invoke(client: Client): Resource<Int> {
        val clientError = ClientError()

        if (client.name?.isEmpty() == true)
            clientError.nameError = R.string.field_empty_error

        if (client.name?.isLettersOrWhiteSpaces() != true)
            clientError.nameError = R.string.invalid_name

        if (client.paternalName?.isEmpty() == true)
            clientError.paternalError = R.string.field_empty_error

        if (client.paternalName?.isLettersOrWhiteSpaces() != true)
            clientError.paternalError = R.string.invalid_paternal_name

        if (client.maternalName?.isLettersOrWhiteSpaces() != true)
            clientError.maternalError = R.string.invalid_maternal_name

        if (client.socialReason?.isLettersWhiteSpacesOrDigits() != true )
            clientError.socialReasonError = R.string.invalid_social_reason

        if (client.rfc?.isLettersOrDigits() != true || client.rfc.length < 12)
            clientError.rfcError = R.string.invalid_rfc

        if (client.phoneNumber.toString().length < 10)
            clientError.phoneError = R.string.invalid_phone

        if (client.email?.isEmpty() == true)
            clientError.emailError = R.string.field_empty_error

        if (client.email?.isValidEmail() != true)
            clientError.emailError = R.string.invalid_email

        if (client.city?.isEmpty() == true)
            clientError.cityError = R.string.field_empty_error

        if (client.city?.isLettersOrWhiteSpaces() != true)
            clientError.cityError = R.string.invalid_city

        if (client.state?.isEmpty() == true)
            clientError.stateError = R.string.field_empty_error

        if (client.state?.isLettersOrWhiteSpaces() != true)
            clientError.stateError = R.string.invalid_state

        if (client.country?.isEmpty() == true)
            clientError.countryError = R.string.field_empty_error

        if (client.country?.isLettersOrWhiteSpaces() != true)
            clientError.countryError = R.string.invalid_country

        val findError = listOfNotNull(
            clientError.nameError, clientError.paternalError, clientError.paternalError,
            clientError.maternalError, clientError.socialReasonError, clientError.rfcError,
            clientError.phoneError, clientError.emailError, clientError.cityError,
            clientError.stateError, clientError.countryError,
        ).isNotEmpty()


        if (findError) return Resource.Error(
            resId = R.string.invalid_client,
            message = Gson().toJson(clientError)
        )
        return Resource.Success()
    }

    private fun String.isLettersOrWhiteSpaces(): Boolean = all {
        it.isLetter() || it == ' '
    }

    private fun String.isLettersOrDigits(): Boolean = all {
        it.isLetterOrDigit()
    }

    private fun String.isLettersWhiteSpacesOrDigits(): Boolean = all {
        it.isLetterOrDigit() || it == ' '
    }

    private fun String.isValidEmail(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}