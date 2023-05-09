package ru.geekbrains.eventsreminder.repo


import ru.geekbrains.eventsreminder.App
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.domain.ResourceState
import ru.geekbrains.eventsreminder.repo.cache.CacheRepo
import ru.geekbrains.eventsreminder.repo.cache.CacheRepoImpl
import ru.geekbrains.eventsreminder.repo.cache.MyContentProvider
import ru.geekbrains.eventsreminder.repo.local.LocalRepo
import ru.geekbrains.eventsreminder.repo.remote.IPhoneCalendarRepo
import ru.geekbrains.eventsreminder.repo.remote.PhoneContactsRepo
import ru.geekbrains.eventsreminder.usecases.deleteDuplicateEvents
import javax.inject.Inject

class RepoImpl @Inject constructor(
    val localRepo: LocalRepo,
    val contactsRepo: PhoneContactsRepo,
    val calendarRepo: IPhoneCalendarRepo
) : Repo {
    companion object {
        // TODO : вынести в di

    }

    override suspend fun loadData(
        daysForShowEvents: Int,
        isDataContact: Boolean,
        isDataCalendar: Boolean
    ): ResourceState<List<EventData>> {
        val listEvents = mutableListOf<EventData>()
        listEvents.addAll(localRepo.getList())
        if (isDataContact) {
            try {
                listEvents.addAll(contactsRepo.loadBirthDayEvents(daysForShowEvents))
            } catch (exc: Throwable) {
                return ResourceState.ErrorState(Throwable("Ошибка заргрузки ДР из телефонной книжки"))
            }
        }
        if (isDataCalendar) {
            try {
                listEvents.addAll(
                    calendarRepo.loadEventCalendar(daysForShowEvents).filter { calendarEvent ->
                        !(calendarEvent.type == EventType.BIRTHDAY && listEvents.any { contactEvent ->
                            contactEvent.birthday?.withYear(0) == calendarEvent.birthday?.withYear(0) &&
                            contactEvent.type == EventType.BIRTHDAY &&
                                    calendarEvent.name.contains(
                                contactEvent.name + " – "
                            )
                        })
                    })
            } catch (exc: Throwable) {
                return ResourceState.ErrorState(Throwable("Ошибка заргрузки событий из календаря"))
            }
        }
        return ResourceState.SuccessState(deleteDuplicateEvents(listEvents.toMutableList()))
    }

}