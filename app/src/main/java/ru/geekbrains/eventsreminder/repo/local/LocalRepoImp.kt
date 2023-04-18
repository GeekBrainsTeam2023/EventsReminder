package ru.geekbrains.eventsreminder.repo.local

import ru.geekbrains.eventsreminder.domain.EventData

class LocalRepoImp:LocalRepo {
    private val listEvent= mutableListOf<EventData>()

    override fun addEvent(event: EventData) {
        listEvent.add(event)
    }

    override fun addListEvents(list: List<EventData>) {
        listEvent.addAll(list)
    }

    override fun getList(): List<EventData> = listEvent.toList()
}