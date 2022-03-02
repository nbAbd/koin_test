package com.pieaksoft.event.consumer.android.model.profile

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Company(
    @SerializedName("id")
    @ColumnInfo(name = "company_id")
    var id: String? = null,

    @SerializedName("name")
    @ColumnInfo(name = "company_name")
    var name: String? = null,

    @SerializedName("mcNumber")
    var mcNumber: String? = null,

    @SerializedName("usDotNumber")
    var usDotNumber: String? = null,

    @SerializedName("address")
    var address: String? = null,

    @SerializedName("state")
    var state: String? = null,

    @SerializedName("city")
    var city: String? = null,

    @SerializedName("postalCode")
    var postalCode: Int? = null,

    @SerializedName("timeZone")
    var timeZone: String? = null,

    @SerializedName("email")
    @ColumnInfo(name = "company_email")
    var email: String? = null,

    @SerializedName("enabled")
    @ColumnInfo(name = "company_enabled_status")
    var enabled: Boolean? = null,

    @SerializedName("usaMultiDayBasis")
    @ColumnInfo(name = "company_usa_multi_day_basis")
    var usaMultiDayBasis: String? = null
) : Parcelable