package com.pieaksoft.event.consumer.android.model.event

import com.google.gson.annotations.SerializedName

data class Certification(
    @SerializedName("date") val date: String?,
    @SerializedName("status") val status: String?
)