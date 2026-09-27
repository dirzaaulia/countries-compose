package com.dirzaaulia.countries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dirzaaulia.countries.platform.StartupState
import com.dirzaaulia.countries.platform.platformApplicationContext
import com.dirzaaulia.countries.ui.app.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        StartupState.isReady.set(false)
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        platformApplicationContext = applicationContext
        splashScreen.setKeepOnScreenCondition { !StartupState.isReady.get() }
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}
