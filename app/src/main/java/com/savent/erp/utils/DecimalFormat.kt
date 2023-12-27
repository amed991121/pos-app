package com.savent.erp.utils

import android.util.Log
import java.text.DecimalFormat

class DecimalFormat{
    companion object{
        private val df = DecimalFormat("0.00")
        fun format(float: Float): String{
            val str = df.format(float).replace(",",".")
            val strBefore = str.substringBefore(".")
            val strAfter = str.substringAfter(".")
            val strBuilder: StringBuilder = StringBuilder()

            for ((count, i) in (strBefore.length - 1 downTo 1).withIndex()){
                strBuilder.apply { insert(0,strBefore[i]) }
                if ((count+1) % 3 == 0 && count != 0)
                    strBuilder.apply { insert(0,",") }

            }
            strBuilder.apply { insert(0,strBefore[0]) }
            return strBuilder.append(".").append(strAfter).toString()
        }
    }
}