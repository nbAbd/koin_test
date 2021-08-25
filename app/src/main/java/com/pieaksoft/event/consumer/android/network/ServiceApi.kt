package com.pieaksoft.event.consumer.android.network

import com.pieaksoft.event.consumer.android.model.AuthModel
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ServiceApi {
    @POST("/api/public/login")
    suspend fun login(@Header("Content-Type") content: String,
                      @Body body: Map<String, String>): AuthModel

}