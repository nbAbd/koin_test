package com.pieaksoft.event.consumer.android.ui.profile

import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.ProfileModel
import com.pieaksoft.event.consumer.android.model.Result
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.network.ServiceApi

class ProfileRepoImpl(private val serviceApi: ServiceApi): ProfileRepo {
    override suspend fun getProfile(): Result<ProfileModel, Exception> {
        return try {
            val response = serviceApi.getProfile()
            Success(response)
        } catch (e: Exception) {
            Failure(e)
        }
    }
}