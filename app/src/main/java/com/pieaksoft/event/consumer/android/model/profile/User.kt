package com.pieaksoft.event.consumer.android.model.profile

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("email")
    @ColumnInfo(name = "user_email")
    var email: String? = null,

    @SerializedName("firstName")
    var firstName: String? = null,

    @SerializedName("lastName")
    var lastName: String? = null,

    @SerializedName("phoneNumber")
    var phoneNumber: String? = null,

    @SerializedName("usaMultiDayBasis")
    var usaMultiDayBasis: String? = null,

    @SerializedName("canadianMultiDayBasis")
    var canadianMultiDayBasis: String? = null,

    @SerializedName("homeTerminalTimezone")
    var homeTerminalTimezone: String? = null,

    @SerializedName("enabled")
    @ColumnInfo(name = "user_enabled_status")
    var enabled: Boolean? = null,

    @SerializedName("exempt")
    var exempt: Boolean? = null,

    @SerializedName("fileStorageLocation")
    var fileStorageLocation: String? = null
) : Parcelable