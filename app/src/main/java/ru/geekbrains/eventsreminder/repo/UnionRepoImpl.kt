package ru.geekbrains.eventsreminder.repo

import ru.geekbrains.eventsreminder.domain.AppState
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.repo.local.ILocalRepo
import ru.geekbrains.eventsreminder.repo.remote.IPhoneCalendarRepo
import ru.geekbrains.eventsreminder.repo.remote.IPhoneContactsRepo

class UnionRepoImpl(
    val settings: SettingsData,
    val localRepo: ILocalRepo,
    val contactsRepo: IPhoneContactsRepo,
    val calendarRepo: IPhoneCalendarRepo
):IUnionRepo {
    override suspend fun loadData(): AppState {
        val listEvents = mutableListOf<EventData>()
        listEvents.addAll(localRepo.getList())
        if (settings.isDataContact) {
            listEvents.addAll(contactsRepo.loadBirthDayEvents(settings.daysForShowEvents))
        }
        if (settings.isDataCalendar) {
            listEvents.addAll(calendarRepo.loadEventCalendar(settings.daysForShowEvents))
        }
        if (listEvents.size > 0) return AppState.SuccessState(listEvents.toList())
         else return AppState.ErrorState(Throwable("Ошибка заргрузки данных"))
    }
}