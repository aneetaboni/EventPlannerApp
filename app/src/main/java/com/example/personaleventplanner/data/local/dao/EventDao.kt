package com.example.personaleventplanner.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.personaleventplanner.data.local.entity.EventEntity

@Dao
interface EventDao {

    @Insert
    suspend fun insertEvent(event: EventEntity)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("SELECT * FROM events ORDER BY dateTime ASC")
    fun getAllEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: Int): EventEntity
}