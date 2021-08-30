package com.pieaksoft.event.consumer.android.model

import java.io.Serializable

data class ProfileModel(
    val id: String?,
    val user: UserModel?,
    val company: CompanyModel?
): Serializable