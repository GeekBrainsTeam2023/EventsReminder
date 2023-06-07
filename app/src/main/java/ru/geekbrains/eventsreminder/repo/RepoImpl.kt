package ru.geekbrains.eventsreminder.repo


import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.domain.ResourceState
import ru.geekbrains.eventsreminder.presentation.ui.safeWithYear
import ru.geekbrains.eventsreminder.repo.local.LocalRepo
import ru.geekbrains.eventsreminder.repo.remote.IPhoneCalendarRepo
import ru.geekbrains.eventsreminder.repo.remote.PhoneContactsRepo
import javax.inject.Inject

class RepoImpl @Inject constructor(
	private val localRepo: LocalRepo,
	private val contactsRepo: PhoneContactsRepo,
	private val calendarRepo: IPhoneCalendarRepo
) : Repo {
	@Throws(Throwable::class)
	override suspend fun loadData(
		daysForShowEvents: Int,
		isDataContact: Boolean,
		isDataCalendar: Boolean
	): ResourceState<List<EventData>> {
		val listEvents = mutableListOf<EventData>()
		if (isDataContact) {
			try {
				listEvents.addAll(contactsRepo.loadBirthDayEvents(daysForShowEvents))
			} catch (exc: Throwable) {
				return ResourceState.ErrorState(
					Throwable(
						"Ошибка заргрузки ДР из телефонной книжки",
						exc
					)
				)
			}
		}
		if (isDataCalendar) {
			try {
				listEvents.addAll(
					calendarRepo.loadEventCalendar(daysForShowEvents).filter { calendarEvent ->
						!(calendarEvent.type == EventType.BIRTHDAY && listEvents.any { contactEvent ->
							contactEvent.birthday?.safeWithYear(0) == calendarEvent.birthday?.safeWithYear(0) &&
									contactEvent.type == EventType.BIRTHDAY &&
									calendarEvent.name.contains(
										contactEvent.name + " – "
									)
						})
					})
			} catch (exc: Throwable) {
				return ResourceState.ErrorState(
					Throwable(
						"Ошибка заргрузки событий из календаря",
						exc
					)
				)
			}
		}
		try {
			listEvents.addAll(localRepo.getList())
		} catch (t: Throwable) {
			return ResourceState.ErrorState(
				Throwable(
					"Ошибка заргрузки событий из списка пользователя",
					t
				)
			)
		}
		return ResourceState.SuccessState(listEvents.toList())
	}

	@Throws(Throwable::class)
	override suspend fun loadLocalData(): ResourceState<List<EventData>> {
		val listEvents = mutableListOf<EventData>()
		try {
			listEvents.addAll(localRepo.getList())
		} catch (t: Throwable) {
			return ResourceState.ErrorState(
				Throwable(
					"Ошибка заргрузки событий из списка пользователя",
					t
				)
			)
		}
		return ResourceState.SuccessState(listEvents.toList())
	}

	@Throws(Throwable::class)
	override suspend fun deleteLocalEvent(eventData: EventData) {
		try {
			localRepo.deleteEvent(eventData)
		} catch (t: Throwable) {
			throw Throwable("Ошибка удаления события из списка пользователя", t)
		}
	}

	@Throws(Throwable::class)
	override suspend fun addLocalEvent(eventData: EventData) {
		try {
			localRepo.addEvent(eventData)
		} catch (t: Throwable) {
			throw Throwable("Ошибка добавления события в список пользователя", t)
		}
	}

	@Throws(Throwable::class)
	override suspend fun clearAllLocalEvents() {
		try {
			localRepo.clear()
		} catch (t: Throwable) {
			throw Throwable("Ошибка удаления событий из списка пользователя", t)
		}
	}
}