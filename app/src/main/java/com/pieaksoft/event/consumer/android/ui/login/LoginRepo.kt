package com.pieaksoft.event.consumer.android.ui.login

import com.pieaksoft.event.consumer.android.model.AuthModel
import com.pieaksoft.event.consumer.android.model.Result

interface LoginRepo {
       suspend fun login(email: String, password: String): Result<AuthModel, Exception>
}