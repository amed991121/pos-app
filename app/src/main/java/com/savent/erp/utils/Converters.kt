package com.savent.erp.utils

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun toLatLng(latLng: String): LatLng =
        Gson().fromJson(latLng, object : TypeToken<LatLng>() {}.type)

    @TypeConverter
    fun fromLatLng(latLng: LatLng): String = Gson().toJson(latLng)

    @TypeConverter
    fun toSelectedProducts(selectedProducts: String): HashMap<Int,Int> =
        Gson().fromJson(selectedProducts, object : TypeToken<HashMap<Int,Int>>() {}.type)

    @TypeConverter
    fun fromSelectedProducts(selectedProducts: HashMap<Int,Int>): String =
        Gson().toJson(selectedProducts)

    @TypeConverter
    fun toDateTimeObj(value: String): DateTimeObj =
        Gson().fromJson(value, object : TypeToken<DateTimeObj>() {}.type)

    @TypeConverter
    fun fromDateTimeObj(dateTimeObj: DateTimeObj): String = Gson().toJson(dateTimeObj)


    /*@TypeConverter
    fun toPendingRemoteAction(value: String): PendingRemoteAction =
       enumValueOf(value)

    @TypeConverter
    fun fromPendingRemoteAction(value: PendingRemoteAction): String = value.name*/





}