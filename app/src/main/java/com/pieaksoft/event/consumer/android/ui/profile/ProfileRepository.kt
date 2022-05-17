package com.pieaksoft.event.consumer.android.ui.profile

import com.pieaksoft.event.consumer.android.model.profile.Profile
import com.pieaksoft.event.consumer.android.model.Result
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(token: String): Result<Profile, Exception>

    // room methods
    suspend fun saveProfile(profile: Profile)

    suspend fun update(profile: Profile)

    suspend fun getPrimaryProfile(): Profile?

    suspend fun getAdditionalProfile(): Profile?

    fun getProfileById(id: String): Flow<Profile>

    suspend fun deletePrimaryProfiles()

    suspend fun deleteAdditionalProfiles()

    suspend fun deleteProfileById(id: String)

    suspend fun isProfileExists(id: String): Boolean
}