package com.pieaksoft.event.consumer.android.model

import java.io.Serializable

data class ProfileModel(
    val id: String?,
    val displayName: String?,
    val userName: String?,
    val email: String?,
    val phoneNumber: String?,
    val profileImage: String?
): Serializable