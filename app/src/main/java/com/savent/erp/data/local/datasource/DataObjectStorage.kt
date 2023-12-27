package com.savent.erp.data.local.datasource

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.gson.Gson
import com.savent.erp.AppConstants
import com.savent.erp.R
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.*
import java.lang.reflect.Type

class DataObjectStorage<T> constructor(
    private val gson: Gson,
    private val type: Type,
    private val dataStore: DataStore<Preferences>,
    private val preferenceKey: Preferences.Key<String>
) {

    suspend fun saveData(data: T): Resource<Int> {
        try {
            dataStore.edit {
                val jsonString = gson.toJson(data, type)
                it[preferenceKey] = jsonString
            }
        } catch (e: Exception) {
            val resId = R.string.save_data_error
            return Resource.Error(resId = resId)
        }
        return Resource.Success()
    }

    fun getData(): Flow<Resource<T>> = flow {
        dataStore.data.map { preferences ->
            val jsonString = preferences[preferenceKey] ?: AppConstants.EMPTY_JSON_STRING
            val elements = gson.fromJson<T>(jsonString, type)
            elements
        }.catch {
            val resId = R.string.retrieve_data_error
            emit(Resource.Error<T>(resId = resId))
        }.collect { emit(Resource.Success(it)) }

    }

    suspend fun clear(): Resource<Int> {
        try {
            dataStore.edit { it.clear() }
        } catch (e: Exception) {
            val resId = R.string.delete_login_credentials_error
            return Resource.Error(resId = resId)
        }
        return Resource.Success()
    }

}