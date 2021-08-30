package com.pieaksoft.event.consumer.android.model

import java.io.Serializable

data class ProfileModel(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val username: String?,
    val profileImage: String?
): Serializable