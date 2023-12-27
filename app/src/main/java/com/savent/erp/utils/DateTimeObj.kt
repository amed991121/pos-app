package com.savent.erp.utils

data class DateTimeObj(val date: String, val time: String){

    companion object{
        fun fromLong(long: Long) =
            DateTimeObj(
                DateFormat.format(long, "yyyy-MM-dd"),
                DateFormat.format(long, "HH:mm:ss.SSS")
            )
    }

}