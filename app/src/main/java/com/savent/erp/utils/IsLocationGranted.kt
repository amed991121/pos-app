package com.savent.erp.utils

import android.content.Context
import com.savent.erp.AppConstants

class IsLocationGranted {
    companion object{
        operator fun invoke(context: Context): Boolean =
            CheckPermissions.check(context,AppConstants.LOCATION_PERMISSIONS)
    }
}