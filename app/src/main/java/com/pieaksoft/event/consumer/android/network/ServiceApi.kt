package com.pieaksoft.event.consumer.android.network

import com.pieaksoft.event.consumer.android.model.auth.AuthModel
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.model.profile.Profile
import com.pieaksoft.event.consumer.android.model.report.Report
import retrofit2.http.*

interface ServiceApi {
    @Headers("Content-Type: application/json")
    @POST("/api/public/login")
    suspend fun login(@Body body: Map<String, String>): AuthModel


    @Headers("isAuthorizable: false")
    @GET("/api/me")
    suspend fun getProfile(@Header("Authorization") token: String): Profile

    @POST("api/event")
    suspend fun insertEvent(@Body body: Event): Event

    @POST("api/event")
    suspend fun certifyEvent(@Body body: Event): Event

    @GET("api/event")
    suspend fun getEventList(): List<Event>

    @POST("api/report")
    suspend fun sendReport(@Body report: Report)
}