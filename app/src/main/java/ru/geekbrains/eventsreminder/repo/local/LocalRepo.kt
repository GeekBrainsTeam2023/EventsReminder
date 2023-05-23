package ru.geekbrains.eventsreminder.repo.local

import ru.geekbrains.eventsreminder.domain.EventData

interface LocalRepo {
    fun addEvent(event:EventData)
//    fun updateEvent(event: EventData)
//    fun getEvent(sourceId : Long) : EventData
    fun deleteEvent(event: EventData)
    fun clear()
    fun getList():List<EventData>
}