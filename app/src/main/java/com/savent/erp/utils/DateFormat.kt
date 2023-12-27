package com.savent.erp.utils

import java.text.SimpleDateFormat
import java.util.*

class DateFormat() {

    companion object {
        fun format(timestamp: Long, format: String): String {
            val simpleDateFormat = SimpleDateFormat(format, Locale.ENGLISH)
            return simpleDateFormat.format(Date(timestamp))
        }

    }


}