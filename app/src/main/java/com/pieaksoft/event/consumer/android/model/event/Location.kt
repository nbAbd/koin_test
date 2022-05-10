package com.pieaksoft.event.consumer.android.model.event

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Location(
    @SerializedName("latitude") val latitude: Float?,
    @SerializedName("longitude") val longitude: Float?
):Serializable