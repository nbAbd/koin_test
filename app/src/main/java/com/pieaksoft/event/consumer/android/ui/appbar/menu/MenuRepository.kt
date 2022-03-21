package com.pieaksoft.event.consumer.android.ui.appbar.menu

import okhttp3.MultipartBody
import java.lang.Exception
import com.pieaksoft.event.consumer.android.model.Result
import okhttp3.ResponseBody

interface MenuRepository {
    suspend fun uploadSignature(file: MultipartBody.Part):Result<ResponseBody,Exception>
    suspend fun downloadSignature(token:String): Result<ResponseBody,Exception>
}