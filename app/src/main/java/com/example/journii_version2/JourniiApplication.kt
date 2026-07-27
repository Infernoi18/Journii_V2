package com.example.journii_version2

import android.app.Application
import android.os.StrictMode
import com.example.journii_version2.core.di.AppContainer

class JourniiApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        enableStrictModeInDebugOnly()
        appContainer = AppContainer(this)
    }

    private fun enableStrictModeInDebugOnly() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }
}
