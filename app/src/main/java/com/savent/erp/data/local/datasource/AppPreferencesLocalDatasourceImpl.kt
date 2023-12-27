package com.savent.erp.data.local.datasource

import com.savent.erp.data.local.model.AppPreferences
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class AppPreferencesLocalDatasourceImpl(
    private val dataObjectStorage: DataObjectStorage<AppPreferences>
): AppPreferencesLocalDatasource {

    override suspend fun insertOrUpdateAppPreferences(preferences: AppPreferences): Resource<Int> =
        dataObjectStorage.saveData(preferences)

    override fun getAppPreferences(): Flow<Resource<AppPreferences>> = flow  {
        dataObjectStorage.getData().onEach { emit(it) }.collect()
    }

    override suspend fun deleteAppPreferences(): Resource<Int> =
        dataObjectStorage.clear()
}