package com.pieaksoft.event.consumer.android.ui.appbar.menu

import com.pieaksoft.event.consumer.android.db.AppDataBase
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.profile.Profile
import com.pieaksoft.event.consumer.android.model.Result
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.network.ServiceApi
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.Response
import java.lang.Exception

class MenuRepositoryImpl(
    private val serviceApi: ServiceApi,
    private val appDataBase: AppDataBase
) :
    MenuRepository {
    override suspend fun uploadSignature(file: MultipartBody.Part):Result<MultipartBody.Part,Exception> {
        return try {
            val response = serviceApi.uploadSignature(file)
            Success(response)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun downloadSignature(userId: String): Result<MultipartBody.Part,Exception> {
        return try {
            val response = serviceApi.downloadSignature(userId)
            Success(response)
        } catch (e: Exception) {
            Failure(e)
        }
    }

}