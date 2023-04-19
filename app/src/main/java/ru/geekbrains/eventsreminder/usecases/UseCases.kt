package ru.geekbrains.eventsreminder.usecases

import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.ResourceState
import ru.geekbrains.eventsreminder.repo.Repo

class UseCases (val repo:Repo){
    suspend fun loadListEvent():ResourceState<List<EventData>>{
        val loadResponse = repo.loadData()
        when(loadResponse){
            is ResourceState.SuccessState -> {
                return ResourceState.SuccessState(getActualListEvents(loadResponse.data,repo.getSettings()))
            }
            is ResourceState.ErrorState -> return loadResponse
        }
    }
}