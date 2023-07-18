package com.morphia.app.utils

import android.util.Log
import com.morphia.app.BuildConfig

class LogX {

    companion object {

        fun E(tag: String, message: String) {
            if (BuildConfig.DEBUG ) {
                Log.e(tag, "" + message)
            }
        }

        fun E(tag: String, message: String, e: Exception?) {
            if (BuildConfig.DEBUG ) {
                Log.e(tag, "" + message, e)
            }
        }


        fun D(tag: String, message: String) {
            if (BuildConfig.DEBUG ) {
                Log.d(tag, "" + message)
            }
        }

        fun V(tag: String, message: String) {
            if (BuildConfig.DEBUG ) {
                Log.v(tag, "" + message)
            }
        }

        fun W(tag: String, message: String) {
            if (BuildConfig.DEBUG ) {
                Log.w(tag, "" + message)
            }
        }

        fun I(tag: String, message: String) {
            if (BuildConfig.DEBUG ) {
                Log.i(tag, "" + message)
            }
        }
    }
}