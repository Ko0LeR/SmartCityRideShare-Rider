package com.tlabs.smartcity.rideshare.ridesharerider

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox

class DriverApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox))
    }
}