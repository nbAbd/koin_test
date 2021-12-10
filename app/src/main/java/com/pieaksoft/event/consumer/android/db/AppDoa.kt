package com.pieaksoft.event.consumer.android.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pieaksoft.event.consumer.android.model.Event

@Dao
interface AppDoa {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveEvent(event: Event)

    @Query("SELECT * FROM event")
    fun getAllEvents(): List<Event>

}