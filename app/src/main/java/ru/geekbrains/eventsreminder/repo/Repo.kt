package ru.geekbrains.eventsreminder.repo

import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.ResourceState

interface Repo {
    suspend fun loadData(daysForShowEvents:Int,isDataContact:Boolean,isDataCalendar:Boolean): ResourceState<List<EventData>>
}