package com.pieaksoft.event.consumer.android.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pieaksoft.event.consumer.android.model.Certification

class CertificationListConverter {
    @TypeConverter
    fun fromCertifications(busStops: List<Certification>): String? {
        return Gson().toJson(busStops)
    }

    @TypeConverter
    fun toCertifications(data: String): List<Certification>? {
        val objects =
            Gson().fromJson(data, Array<Certification>::class.java) as Array<Certification>
        return objects.toList()
    }
}