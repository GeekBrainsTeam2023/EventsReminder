package ru.geekbrains.eventsreminder.repo.cache

import kotlinx.coroutines.flow.Flow
import ru.geekbrains.eventsreminder.domain.EventData
import java.time.LocalDate

interface CacheRepo {
    fun getList():List<EventData>
    fun renew(events: List<EventData>)

}

