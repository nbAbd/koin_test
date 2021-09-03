package com.pieaksoft.event.consumer.android.network

import android.content.SharedPreferences
import android.util.Log
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_CURRENT_USER_ID
import okhttp3.Interceptor
import okhttp3.Response
import org.koin.core.KoinComponent
import org.koin.core.inject

class OkHttpInterceptor : Interceptor, KoinComponent {
    private val sharedPreferences: SharedPreferences by inject()

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.run {
            val request = request().newBuilder()

            sharedPreferences.getString(SHARED_PREFERENCES_CURRENT_USER_ID, "")?.let {
                if(it.isNotEmpty()){
                    request.header("Authorization", "Bearer $it")
                    Log.wtf("Token", it)
                }
            }

            val response = proceed(request.build())
            Log.e("request", "Request: ${request.build()}")
            Log.e("request", "Request response: ${response}")
            response
        }
    }
}