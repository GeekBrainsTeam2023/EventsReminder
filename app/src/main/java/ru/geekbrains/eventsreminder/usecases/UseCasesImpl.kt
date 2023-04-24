package ru.geekbrains.eventsreminder.usecases

import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.ResourceState
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.repo.Repo

class UseCasesImpl (val repo:Repo):UseCases{
    override suspend fun loadListEvent(settings: SettingsData):ResourceState<List<EventData>>{
        val loadResponse = repo.loadData(settings.daysForShowEvents)
        when(loadResponse){
            is ResourceState.SuccessState -> {
                return ResourceState.SuccessState(getActualListEvents(loadResponse.data,settings))
            }
            is ResourceState.ErrorState -> return loadResponse
        }
    }
}