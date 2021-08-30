package com.pieaksoft.event.consumer.android.ui.profile

import com.pieaksoft.event.consumer.android.model.ProfileModel
import com.pieaksoft.event.consumer.android.model.Result

interface ProfileRepo {
    suspend fun getProfile(token: String): Result<ProfileModel, Exception>
}