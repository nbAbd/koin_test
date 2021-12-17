package com.pieaksoft.event.consumer.android.ui.login

import com.pieaksoft.event.consumer.android.model.AuthModel
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Result
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.network.ServiceApi

class LoginRepoImpl(private val serviceApi: ServiceApi): LoginRepo {
    override suspend fun login(email: String, password: String): Result<AuthModel, Exception> {
        return try {
            val body: MutableMap<String, String> = mutableMapOf()
            body["email"] = email
            body["password"] = password
            val response = serviceApi.login(body = body)
            Success(response)
        } catch (e: Exception) {
            Failure(e)
        }
    }
}