package com.pieaksoft.event.consumer.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pieaksoft.event.consumer.android.model.event.Event

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveEvent(event: Event)

    @Query("SELECT * FROM event")
    fun getAllEvents(): List<Event>

    @Query("DELETE FROM event")
    fun deleteEvents()
}