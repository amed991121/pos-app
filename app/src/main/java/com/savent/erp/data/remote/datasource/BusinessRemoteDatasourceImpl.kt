package com.savent.erp.data.remote.datasource


import android.util.Log
import com.google.gson.Gson
import com.savent.erp.AppConstants
import com.savent.erp.data.remote.model.Business
import com.savent.erp.data.remote.model.LoginCredentials
import com.savent.erp.data.remote.service.BusinessApiService
import com.savent.erp.utils.Resource
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*
import javax.security.cert.CertificateException


class BusinessRemoteDatasourceImpl(
    private val businessApiService: BusinessApiService
) : BusinessRemoteDatasource {

    override suspend fun getBusiness(credentials: LoginCredentials, storeId: Int, companyId: Int):
            Resource<Business> {
        try {
            val response =
                businessApiService.getBusiness(Gson().toJson(credentials), storeId, companyId)
            //Log.d("log_",response.toString())
            if (response.isSuccessful)
                return Resource.Success(response.body())
            return Resource.Error(message = response.message().toString())
        } catch (e: Exception) {
            //Log.d("log_",e.toString())
            return Resource.Error(message = "ConnectionError")
        }


    }
}