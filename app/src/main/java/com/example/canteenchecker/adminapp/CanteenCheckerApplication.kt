package com.example.canteenchecker.adminapp

import android.app.Application

class CanteenCheckerApplication : Application() {

    var authenticationToken: String? = null

    override fun onCreate() {
        super.onCreate()
    }
}