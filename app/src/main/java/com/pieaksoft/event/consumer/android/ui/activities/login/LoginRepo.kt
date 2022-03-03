package com.pieaksoft.event.consumer.android.ui.activities.login

import com.pieaksoft.event.consumer.android.model.Result
import com.pieaksoft.event.consumer.android.model.auth.AuthModel

interface LoginRepo {
       suspend fun login(email: String, password: String): Result<AuthModel, Exception>
}