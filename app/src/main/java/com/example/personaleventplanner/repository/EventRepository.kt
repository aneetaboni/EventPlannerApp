package com.example.personaleventplanner.repository

import com.example.personaleventplanner.data.local.dao.EventDao
import com.example.personaleventplanner.data.local.entity.EventEntity

class EventRepository(private val eventDao: EventDao) {

    val allEvents = eventDao.getAllEvents()

    suspend fun insert(event: EventEntity) {
        eventDao.insertEvent(event)
    }

    suspend fun update(event: EventEntity) {
        eventDao.updateEvent(event)
    }

    suspend fun delete(event: EventEntity) {
        eventDao.deleteEvent(event)
    }

    suspend fun getEventById(id: Int): EventEntity {
        return eventDao.getEventById(id)
    }
}