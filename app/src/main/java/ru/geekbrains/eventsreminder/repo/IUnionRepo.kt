package ru.geekbrains.eventsreminder.repo

import ru.geekbrains.eventsreminder.domain.AppState

interface IUnionRepo {
    suspend fun loadData(): AppState
}