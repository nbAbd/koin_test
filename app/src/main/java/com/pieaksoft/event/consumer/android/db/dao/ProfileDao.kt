package com.pieaksoft.event.consumer.android.db.dao

import androidx.room.*
import com.pieaksoft.event.consumer.android.model.profile.Profile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: Profile)

    @Update
    suspend fun updateProfile(profile: Profile)

    @Query("SELECT * FROM PROFILE where isAdditional =0 LIMIT 1")
    fun getPrimaryProfile(): Profile?

    @Query("SELECT * FROM PROFILE WHERE isAdditional = 1 LIMIT 1")
    fun getAdditionalProfile(): Profile?

    @Query("SELECT *FROM PROFILE WHERE profile_id = :id")
    fun getProfileById(id: String): Flow<Profile>

    @Query("DELETE FROM PROFILE WHERE isAdditional = 0")
    suspend fun deletePrimaryProfiles()

    @Query("DELETE FROM PROFILE WHERE isAdditional = 1")
    suspend fun deleteAdditionalProfiles()

    @Query("DELETE FROM PROFILE WHERE profile_id = :id")
    suspend fun deleteProfileById(id: String)

    @Query("SELECT CASE WHEN EXISTS(SELECT *FROM PROFILE WHERE profile_id = :id) THEN 'TRUE' ELSE 'FALSE' END")
    suspend fun isProfileExists(id: String): Boolean
}