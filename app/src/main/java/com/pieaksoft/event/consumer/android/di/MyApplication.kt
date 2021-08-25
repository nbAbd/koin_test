package com.pieaksoft.event.consumer.android.di

import android.app.Application
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.FirebaseApp
import com.pieaksoft.event.consumer.android.utils.koinAppDeclaration
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    companion object {
        var appContext: Context? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        koinAppDeclaration = {
            androidContext(this@MyApplication)
            modules(listOf(RetrofitModule, AppModule))
        }
        Fresco.initialize(applicationContext)
        startKoin(koinAppDeclaration ?: return)
    }
}