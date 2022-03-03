package com.pieaksoft.event.consumer.android.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pieaksoft.event.consumer.android.db.converters.CertificationConverter
import com.pieaksoft.event.consumer.android.db.converters.CertificationListConverter
import com.pieaksoft.event.consumer.android.db.converters.LocationConverter
import com.pieaksoft.event.consumer.android.db.dao.AppDao
import com.pieaksoft.event.consumer.android.db.dao.ProfileDao
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.model.profile.Converters
import com.pieaksoft.event.consumer.android.model.profile.Profile

@Database(
    entities = [
        Event::class,
        Profile::class
    ],
    version = 1
)

@TypeConverters(
    CertificationConverter::class,
    CertificationListConverter::class,
    LocationConverter::class,
    Converters::class
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getAppDao(): AppDao

    abstract fun getProfileDao(): ProfileDao
}