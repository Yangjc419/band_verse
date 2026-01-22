package com.bandverse.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BandverseApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}
