package com.pieaksoft.event.consumer.android.model.profile

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Vehicle(
    @SerializedName(value = "id")
    @ColumnInfo(name = "vehicle_id")
    val id: Int?,

    @ColumnInfo(name = "vehicle_name")
    val name: String?
) : Parcelable, Serializable
