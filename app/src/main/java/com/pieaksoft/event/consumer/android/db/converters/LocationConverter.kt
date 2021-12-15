package com.pieaksoft.event.consumer.android.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pieaksoft.event.consumer.android.model.Location

class LocationConverter {
    @TypeConverter
    fun fromLocation(location: Location?): String? {
        return Gson().toJson(location)
    }

    @TypeConverter
    fun toLocation(data: String?): Location? {
        return Gson().fromJson(data, Location::class.java) as Location?
    }
}