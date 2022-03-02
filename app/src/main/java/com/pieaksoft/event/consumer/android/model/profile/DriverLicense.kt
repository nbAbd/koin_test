package com.pieaksoft.event.consumer.android.model.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DriverLicense(
    val number: String?,
    val issuedDate: String?
) : Parcelable
