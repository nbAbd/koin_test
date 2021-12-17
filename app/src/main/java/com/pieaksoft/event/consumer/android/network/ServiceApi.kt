package com.pieaksoft.event.consumer.android.network

import com.pieaksoft.event.consumer.android.model.AuthModel
import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.model.ProfileModel
import retrofit2.http.*

interface ServiceApi {
    @Headers("Content-Type: application/json")
    @POST("/api/public/login")
    suspend fun login(@Body body: Map<String, String>): AuthModel


    @Headers("isAuthorizable: false")
    @GET("/api/me")
    suspend fun getProfile(@Header("Authorization") token: String): ProfileModel

    @POST("api/event")
    suspend fun insertEvent(@Body body: Event): Event

    @POST("api/event")
    suspend fun certifyEvent(@Body body: Event): Event

    @GET("api/event")
    suspend fun getEventList(): List<Event>

}