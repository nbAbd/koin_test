package com.pieaksoft.event.consumer.android.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pieaksoft.event.consumer.android.model.Certification

class CertificationConverter {
    @TypeConverter
    fun fromLocation(certification: Certification): String {
        return Gson().toJson(certification)
    }

    @TypeConverter
    fun toLocation(data: String): Certification {
        return Gson().fromJson(data, Certification::class.java) as Certification
    }
}