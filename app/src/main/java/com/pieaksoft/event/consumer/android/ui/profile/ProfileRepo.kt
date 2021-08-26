package com.pieaksoft.event.consumer.android.ui.profile

interface ProfileRepo {
    suspend fun getProfile(token: String)
}