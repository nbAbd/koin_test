package com.pieaksoft.event.consumer.android.model.profile

import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Profile(
    @PrimaryKey
    @SerializedName(value = "id")
    @ColumnInfo(name = "profile_id")
    val id: String,

    @SerializedName(value = "token")
    @ColumnInfo(name = "token")
    var token: String?,

    @Embedded
    val user: User,

    @Embedded
    val company: Company?,

    val authorities: List<String>?,

    @Embedded
    val driverLicense: DriverLicense?,

    @Embedded
    val vehicle: Vehicle?,

    @ColumnInfo(defaultValue = "0")
    var isAdditional: Boolean?
) : Parcelable