package com.morphia.app.app

import android.app.Application

class MorphiaApp : Application() {

    companion object {

        lateinit var instance: MorphiaApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}