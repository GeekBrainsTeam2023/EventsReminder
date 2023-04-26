package ru.geekbrains.eventsreminder.repo

import ru.geekbrains.eventsreminder.domain.AppState
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.ResourceState
import ru.geekbrains.eventsreminder.domain.SettingsData

interface Repo {
    suspend fun loadData(daysForShowEvents:Int,isDataContact:Boolean,isDataCalendar:Boolean): ResourceState<List<EventData>>
}