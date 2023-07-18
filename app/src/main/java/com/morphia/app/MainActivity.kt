package com.morphia.app

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.morphia.app.utils.LogX
import com.morphia.app.base.BaseActivity
import com.morphia.app.R

class MainActivity : BaseActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            LogX.E(TAG, "current = $controller.currentDestination , destination = $destination ")
        }
    }
}