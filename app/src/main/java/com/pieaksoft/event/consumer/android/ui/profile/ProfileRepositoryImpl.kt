package com.pieaksoft.event.consumer.android.ui.profile

import com.pieaksoft.event.consumer.android.db.AppDataBase
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.profile.Profile
import com.pieaksoft.event.consumer.android.model.Result
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.network.ServiceApi
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    private val serviceApi: ServiceApi,
    private val appDataBase: AppDataBase
) :
    ProfileRepository {
    override suspend fun getProfile(token: String): Result<Profile, Exception> {
        return try {
            val response = serviceApi.getProfile("Bearer $token")
            Success(response)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun saveProfile(profile: Profile) {
        appDataBase.getProfileDao().saveProfile(profile = profile)
    }

    override suspend fun update(profile: Profile) {
        appDataBase.getProfileDao().updateProfile(profile = profile)
    }

    override suspend fun getPrimaryProfile(): Profile? {
        return appDataBase.getProfileDao().getPrimaryProfile()
    }

    override suspend fun getAdditionalProfile(): Profile? {
        return appDataBase.getProfileDao().getAdditionalProfile()
    }

    override fun getProfileById(id: String): Flow<Profile> {
        return appDataBase.getProfileDao().getProfileById(id = id)
    }

    override suspend fun deleteAdditionalProfiles() {
        appDataBase.getProfileDao().deleteAdditionalProfiles()
    }

    override suspend fun deletePrimaryProfiles() {
        appDataBase.getProfileDao().deletePrimaryProfiles()
    }

    override suspend fun deleteProfileById(id: String) {
        appDataBase.getProfileDao().deleteProfileById(id = id)
    }

    override suspend fun isProfileExists(id: String): Boolean {
        return appDataBase.getProfileDao().isProfileExists(id = id)
    }
}