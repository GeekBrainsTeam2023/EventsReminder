package ru.geekbrains.eventsreminder.repo.local

import ru.geekbrains.eventsreminder.domain.EventData

interface ILocalRepo {
    fun addEvent(event:EventData)
    fun addListEvents(listEvent: List<EventData>)
    fun getList():List<EventData>
}