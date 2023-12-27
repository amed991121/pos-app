package com.savent.erp.utils

import java.text.SimpleDateFormat

fun DateTimeObj.toLong() =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("${this.date} ${this.time}").time

