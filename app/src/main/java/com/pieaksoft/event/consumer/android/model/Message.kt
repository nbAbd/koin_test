package com.pieaksoft.event.consumer.android.model

import java.io.Serializable


class Message : Serializable {
    val errors: List<Error>? = null

    inner class Error {
        var detail: String? = null
    }
}