package com.savent.erp.utils

class IsFromToday() {

    companion object {
        operator fun invoke(timestamp: Long): Boolean {
            val today = DateFormat.format(System.currentTimeMillis(), "yyyy-MM-dd")
            val currentDate = DateFormat.format(timestamp, "yyyy-MM-dd")
            return currentDate == today
        }

    }


}