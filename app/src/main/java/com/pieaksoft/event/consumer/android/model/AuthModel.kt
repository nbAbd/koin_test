package com.pieaksoft.event.consumer.android.model

import java.io.Serializable

data class AuthModel(
    val jwtToken: String,
    val authorities: List<String>?
) : Serializable