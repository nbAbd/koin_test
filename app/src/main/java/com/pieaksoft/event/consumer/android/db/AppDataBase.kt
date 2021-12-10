package com.pieaksoft.event.consumer.android.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pieaksoft.event.consumer.android.db.converters.CertificationConverter
import com.pieaksoft.event.consumer.android.db.converters.CertificationListConverter
import com.pieaksoft.event.consumer.android.db.converters.LocationConverter
import com.pieaksoft.event.consumer.android.model.Event

@Database(
    entities = [Event::class],
    version = 1
)

@TypeConverters(CertificationConverter::class, CertificationListConverter::class, LocationConverter::class)
abstract class AppDataBase: RoomDatabase() {

    abstract fun getAppDao(): AppDoa
}