package ru.geekbrains.eventsreminder.repo

import ru.geekbrains.eventsreminder.domain.AppState

interface Repo {
    suspend fun loadData(): AppState
}