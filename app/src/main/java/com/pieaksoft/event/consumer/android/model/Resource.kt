package com.pieaksoft.event.consumer.android.model

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val errorMessage: String) : Resource<Nothing>()
}