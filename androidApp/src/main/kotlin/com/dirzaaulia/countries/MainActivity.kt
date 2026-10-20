package com.dirzaaulia.countries

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.dirzaaulia.countries.platform.StartupState
import com.dirzaaulia.countries.platform.platformApplicationContext
import com.dirzaaulia.countries.ui.app.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        StartupState.isReady.set(false)
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        platformApplicationContext = applicationContext
        com.dirzaaulia.countries.platform.platformOnboardingContext = applicationContext
        com.dirzaaulia.countries.core.dynamicapi.dynamicApiContext = applicationContext
        splashScreen.setKeepOnScreenCondition { !StartupState.isReady.get() }

        // Cinematic zoom-and-fade exit animation matching other suite projects
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val iconView = splashScreenView.iconView

            val scaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 2.2f)
            val scaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 2.2f)
            val alphaIcon = ObjectAnimator.ofFloat(iconView, View.ALPHA, 1f, 0f)
            val alphaView = ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)
            val translateY = ObjectAnimator.ofFloat(splashScreenView.view, View.TRANSLATION_Y, 0f, -60f)

            AnimatorSet().apply {
                playTogether(scaleX, scaleY, alphaIcon, alphaView, translateY)
                duration = 600L
                interpolator = FastOutSlowInInterpolator()
                doOnEnd { splashScreenView.remove() }
                start()
            }
        }

        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}
