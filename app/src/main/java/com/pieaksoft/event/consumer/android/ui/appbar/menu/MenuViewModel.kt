package com.pieaksoft.event.consumer.android.ui.appbar.menu

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.ui.base.BaseViewModel
import com.pieaksoft.event.consumer.android.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.*


class MenuViewModel(val app: Application, private val repository: MenuRepository) :
    BaseViewModel(app) {

    private val _signature = SingleLiveEvent<Bitmap?>()
    val signature: LiveData<Bitmap?> = _signature

    val isUploaded: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun uploadSignature(signatureBitmap: Bitmap) {

        val file = bitmapToFile(signatureBitmap)

        val body: MultipartBody.Part = createFormData(
            "file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
        )

        launch {
            if (isNetworkAvailable) {
                when (val result =
                    withContext(Dispatchers.IO) { repository.uploadSignature(file = body) }) {
                    is Success -> isUploaded.value = true
                    is Failure -> _error.value = result.error
                }
            }
        }
    }

    fun downloadSignature(token: String) {
        launch {
            if (isNetworkAvailable) {
                val result =
                    withContext(Dispatchers.IO) { repository.downloadSignature(token) }

                when (result) {
                    is Success -> result.data.let {
                        _signature.value = BitmapFactory.decodeStream(it.byteStream())
                    }
                    is Failure -> {
                        _error.value = result.error
                        _signature.value = null
                    }
                }
            }
        }
    }

    private fun bitmapToFile(signatureBitmap: Bitmap): File {
        val file = File(context.cacheDir, "file")
        file.createNewFile()
        val bos = ByteArrayOutputStream()
        signatureBitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
        val bitmapData: ByteArray = bos.toByteArray()
        try {
            val fos = FileOutputStream(file)
            fos.apply {
                write(bitmapData)
                flush()
                close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return file
    }
}