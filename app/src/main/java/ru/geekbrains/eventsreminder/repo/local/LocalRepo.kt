package ru.geekbrains.eventsreminder.repo.local

import ru.geekbrains.eventsreminder.domain.EventData

interface LocalRepo {
    fun addEvent(event:EventData)
    fun updateEvent(event: EventData)
    fun deleteEvent(event: EventData)
    fun getList():List<EventData>
}