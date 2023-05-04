package ru.geekbrains.eventsreminder.repo.local

import kotlinx.coroutines.flow.Flow
import ru.geekbrains.eventsreminder.domain.EventData
import java.time.LocalDate

interface CacheRepo {
    fun getList():List<EventData>
    fun retain(events: List<EventData>): List<EventData>
    fun cleanUpToDate(date: LocalDate)
}

