package com.savent.erp.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

class GetUriFromFile {

    companion object{
        operator fun invoke(context: Context, file: File): Uri {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                FileProvider.getUriForFile(context, "com.savent.erp.fileprovider", file)
            else
                Uri.fromFile(file)
        }
    }

}