package com.pieaksoft.event.consumer.android.network

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.model.Message
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


object ErrorHandler {
    @JvmStatic
    fun getErrorMessage(e: Throwable, context: Context): String {
        var errorText = ""
        if (e is HttpException) {
            if (e.response() != null) {
                if (e.response()!!.code() < 500) {
                    try {
                        val message = Gson().fromJson(
                            e.response()?.errorBody()?.string(),
                            Message::class.java
                        )

                        val errorStr = message.message ?: ""

                        errorText = errorStr
                        if (errorText.isEmpty()) errorText =
                            context.getString(R.string.server_error) // в ситуации когда message.getErrors() приходит, но приходит пустым
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                        errorText = context.getString(R.string.json_error)

                    }
                } else {
                    errorText = context.getString(R.string.server_error)

                }
            }
        } else if (e is UnknownHostException) errorText =
            context.getString(R.string.network_error) else if (e is SocketTimeoutException) {
            errorText = context.getString(R.string.network_timeout_error)
        } else {
            errorText = context.getString(R.string.unknown_error)

        }
        return errorText
    }

    @JvmStatic
    fun showError(view: View?, e: Throwable) {
        if (view != null) Snackbar.make(
            view,
            getErrorMessage(e, view.context),
            Snackbar.LENGTH_LONG
        ).show()
    }


}