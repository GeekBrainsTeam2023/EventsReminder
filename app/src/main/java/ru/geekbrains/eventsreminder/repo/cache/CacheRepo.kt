package ru.geekbrains.eventsreminder.repo.cache

import ru.geekbrains.eventsreminder.domain.EventData

interface CacheRepo {
    fun getList():List<EventData>
    fun renew(events: List<EventData>)
}

