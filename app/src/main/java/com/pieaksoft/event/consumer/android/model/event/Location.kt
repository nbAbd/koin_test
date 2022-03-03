package com.pieaksoft.event.consumer.android.model.event

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("latitude") val latitude: Float = 0.0f,
    @SerializedName("longitude") val longitude: Float = 0.0f
)