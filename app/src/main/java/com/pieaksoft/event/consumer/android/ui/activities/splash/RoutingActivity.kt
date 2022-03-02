package com.pieaksoft.event.consumer.android.ui.activities.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.pieaksoft.event.consumer.android.ui.activities.login.LoginActivity
import com.pieaksoft.event.consumer.android.ui.activities.main.MainActivity
import com.pieaksoft.event.consumer.android.ui.profile.ProfileViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class RoutingActivity : AppCompatActivity() {
    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep the splash screen visible for this Activity
        splashScreen.setKeepOnScreenCondition { true }
        determineDirection()
        finish()
    }

    private fun determineDirection() {
        if (profileViewModel.isAuth()) {
            startActivity(MainActivity.newInstance(this))
        } else {
            startActivity(LoginActivity.newInstance(this))
        }
    }
}