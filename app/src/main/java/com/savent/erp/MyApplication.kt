package com.savent.erp

import android.app.Application
import android.util.Log
import com.mazenrashed.printooth.Printooth

import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.domain.usecase.RemoteDataSyncFromLocalUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.android.BuildConfig
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _networkStatus = MutableSharedFlow<ConnectivityObserver.Status>()
    val networkStatus = _networkStatus.asSharedFlow()
    var currentNetworkStatus: ConnectivityObserver.Status? = null

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@MyApplication)
            modules(appModule)
        }
        Printooth.init(this)
        applicationScope.launch {
            performanceNetworkTask()
        }


    }

    private suspend fun performanceNetworkTask() {
        val connectivityObserver = NetworkConnectivityObserver(applicationContext)
        connectivityObserver.observe().collectLatest {
           // Log.d("log_", "connectivity"+ it.toString())
            currentNetworkStatus = it
            _networkStatus.emit(it)
            if (it == ConnectivityObserver.Status.Available) {
                /*businessBasicsRepository.getBusinessBasics().first().data?.let {it1->
                    remoteDataSyncFromLocalUseCase(it1.id, it1.sellerId, it1.storeId, it1.featureName)
                }*/
            }
        }
    }



}