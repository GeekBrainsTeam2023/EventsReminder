package ru.geekbrains.eventsreminder.repo

import ru.geekbrains.eventsreminder.domain.AppState
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.ResourceState
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.repo.local.LocalRepo
import ru.geekbrains.eventsreminder.repo.remote.IPhoneCalendarRepo
import ru.geekbrains.eventsreminder.repo.remote.PhoneContactsRepo

class RepoImpl(
    val settings: SettingsData,
    val localRepo: LocalRepo,
    val contactsRepo: PhoneContactsRepo,
    val calendarRepo: IPhoneCalendarRepo
):Repo {
    override suspend fun loadData(): ResourceState<List<EventData>> {
        val listEvents = mutableListOf<EventData>()
        listEvents.addAll(localRepo.getList())
        if (settings.isDataContact) {
            try {
                listEvents.addAll(contactsRepo.loadBirthDayEvents(settings.daysForShowEvents))
            } catch(exc:Throwable) {
                return ResourceState.ErrorState(Throwable("Ошибка заргрузки ДР из телефонной книжки"))
            }
        }
        if (settings.isDataCalendar) {
            try {
            listEvents.addAll(calendarRepo.loadEventCalendar(settings.daysForShowEvents))
            } catch(exc:Throwable) {
                return ResourceState.ErrorState(Throwable("Ошибка заргрузки событий из календаря"))
            }
        }
        return ResourceState.SuccessState(listEvents.toList())
    }
}