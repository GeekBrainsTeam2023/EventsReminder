package ru.geekbrains.eventsreminder.usecases

import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.ResourceState
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.repo.Repo

class UseCasesImpl (val repo:Repo):UseCases{
    override suspend fun loadListEvent(settings: SettingsData):ResourceState<List<EventData>>{
        return when(val loadResponse = repo.loadData(settings.daysForShowEvents,settings.isDataContact,settings.isDataCalendar)){
            is ResourceState.SuccessState -> {
                ResourceState.SuccessState(getActualListEvents(loadResponse.data,settings))
            }

            is ResourceState.ErrorState -> loadResponse
        }
    }
}