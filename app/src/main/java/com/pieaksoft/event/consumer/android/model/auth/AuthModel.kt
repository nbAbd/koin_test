package com.pieaksoft.event.consumer.android.model.auth

import com.google.gson.annotations.SerializedName

data class AuthModel(
    @SerializedName("jwtToken") val jwtToken: String,
    @SerializedName("authorities") val authorities: List<String>?
)