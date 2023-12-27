package com.savent.erp

import android.Manifest
import com.savent.erp.data.remote.service.ProviderApiService

object AppConstants {
    const val EMPTY_JSON_STRING = "[]"
    const val APP_PREFERENCES = "app_preferences"
    const val LOGIN_CREDENTIALS = "login_credentials_preferences"
    const val BUSINESS_PREFERENCES = "business_basics_preferences"
    const val PENDING_SALE_PREFERENCES = "pending_sale_preferences"
    const val APP_DATABASE_NAME = "app_database"
    const val MAPS_API_KEY = "you_maps_key"
    const val SAVENT_POS_API_BASE_URL = "your_api_base_url"
    const val CLIENTS_API_PATH = "clients/"
    const val PRODUCTS_API_PATH = "products/"
    const val DISCOUNTS_API_PATH = "discounts/"
    const val BUSINESS_API_PATH = "business/"
    const val SALES_API_PATH = "sales/"
    const val MOVEMENTS_API_PATH = "movements/"
    const val MOVEMENT_REASONS_API_PATH = "movements/reasons"
    const val PROVIDERS_API_PATH = "providers/"
    const val EMPLOYEES_API_PATH = "employees/"
    const val PURCHASES_API_PATH = "purchases/"
    const val INCOMPLETE_PAYMENTS_API_PATH = "incomplete_payments/"
    const val DEBT_PAYMENTS_API_PATH = "debt_payments/"
    const val COMPANIES_API_PATH = "companies/"
    const val STORES_API_PATH = "stores/"
    const val AUTHORIZATION = "your_auth"
    val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val ANDROID_12_BLE_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    const val REQUEST_LOCATION_CODE = 8989
    const val REQUEST_12_BLE_CODE = 9090
    const val REQUEST_LOCATION_PERMISSION_CODE = 10
    const val CODE_SCANNER = 1
    const val RESULT_CODE_SCANNER = "result_code_scanner"
}