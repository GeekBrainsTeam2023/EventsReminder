package ru.geekbrains.eventsreminder.repo

import ru.geekbrains.eventsreminder.domain.AppState
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.ResourceState

interface Repo {
    suspend fun loadData(): ResourceState<List<EventData>>
}