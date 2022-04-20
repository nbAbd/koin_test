package com.pieaksoft.event.consumer.android.ui.appbar.menu

import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Result
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.network.ServiceApi
import okhttp3.MultipartBody
import okhttp3.ResponseBody

class MenuRepositoryImpl(private val serviceApi: ServiceApi) : MenuRepository {
    override suspend fun uploadSignature(file: MultipartBody.Part): Result<ResponseBody, Exception> {
        return try {
            val response = serviceApi.uploadSignature(file)
            Success(response)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun downloadSignature(token: String): Result<ResponseBody, Exception> {
        return try {
            val response = serviceApi.downloadSignature("Bearer $token")
            Success(response)
        } catch (e: Exception) {
            Failure(e)
        }
    }
}