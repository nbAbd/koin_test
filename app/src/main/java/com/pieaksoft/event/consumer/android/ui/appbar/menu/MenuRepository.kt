package com.pieaksoft.event.consumer.android.ui.appbar.menu

import okhttp3.MultipartBody
import java.lang.Exception
import com.pieaksoft.event.consumer.android.model.Result

interface MenuRepository {
    suspend fun uploadSignature(file: MultipartBody.Part):Result<MultipartBody.Part,Exception>
    suspend fun downloadSignature(userId: String): Result<MultipartBody.Part,Exception>
}