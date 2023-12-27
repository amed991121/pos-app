package com.savent.erp.data.local.datasource

import com.savent.erp.data.local.model.AppPreferences
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AppPreferencesLocalDatasource {

    suspend fun insertOrUpdateAppPreferences(preferences: AppPreferences): Resource<Int>

    fun getAppPreferences(): Flow<Resource<AppPreferences>>

    suspend fun deleteAppPreferences(): Resource<Int>
}