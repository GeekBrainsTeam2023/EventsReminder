package ru.geekbrains.eventsreminder.usecases

import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.ResourceState
import ru.geekbrains.eventsreminder.repo.Repo

class UseCasesImpl (val repo:Repo):UseCases{
    override suspend fun loadListEvent():ResourceState<List<EventData>>{
        val loadResponse = repo.loadData()
        when(loadResponse){
            is ResourceState.SuccessState -> {
                return ResourceState.SuccessState(getActualListEvents(loadResponse.data,repo.getSettings()))
            }
            is ResourceState.ErrorState -> return loadResponse
        }
    }
}