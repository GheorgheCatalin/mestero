package com.mestero.config

import android.app.Application
import android.content.Context
import com.mestero.utils.Analytics
import com.mestero.utils.LanguageManager
import com.google.firebase.perf.FirebasePerformance
import dagger.hilt.android.HiltAndroidApp

// Required for Hilt DI setup
@HiltAndroidApp
class MesteroApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Start Firebase Performance monitoring
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
        
        // Start measuring app cold start time
        Analytics.logAppStart()
    }
    
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { LanguageManager.applyLanguage(it) })
    }
}