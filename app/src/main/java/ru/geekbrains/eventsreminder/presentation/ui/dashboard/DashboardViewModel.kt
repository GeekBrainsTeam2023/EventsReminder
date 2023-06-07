package ru.geekbrains.eventsreminder.presentation.ui.dashboard

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.geekbrains.eventsreminder.domain.AppState
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.domain.ResourceState
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.presentation.ui.safeWithYear
import ru.geekbrains.eventsreminder.repo.Repo
import ru.geekbrains.eventsreminder.repo.cache.CacheRepo
import java.lang.Integer.max
import java.time.LocalDate
import javax.inject.Inject


class DashboardViewModel @Inject constructor(
	private val settingsData: SettingsData,
	private val repo: Repo,
	private val cacheRepo: CacheRepo
) : ViewModel(), LifecycleObserver {
	val statesLiveData: MutableLiveData<AppState> = MutableLiveData()
	private var allEventsFromRepo = listOf<EventData>()
	private val cachedEventsList = mutableListOf<EventData>()
	private val viewModelCoroutineScope = CoroutineScope(
		Dispatchers.IO
				+ SupervisorJob()
				+ CoroutineExceptionHandler { _, throwable -> handleError(throwable) }
	)
	private var eventsListJob: Job? = null

	/**
	 * Используется для хранения списка из DashboardRecyclerViewAdapter
	 * */
	val storedFilteredEvents = mutableListOf<EventData>()
	fun handleError(error: Throwable) {
		try {
			statesLiveData.postValue(AppState.ErrorState(error))
		} catch (_: Throwable) {
		}
	}

	override fun onCleared() {
		try {
			super.onCleared()
			viewModelCoroutineScope.cancel()
		} catch (t: Throwable) {
			handleError(t)
		}
	}

	private fun List<EventData>.filterToDashboard() =
		try {
			takeWhile { eventData ->
				eventData.date <= LocalDate.now()
					.plusDays(settingsData.daysForShowEvents.toLong())
			}
		} catch (t: Throwable) {
			handleError(t)
			listOf()
		}

	fun loadEvents() {
		try {
			if (cachedEventsList.any())
				statesLiveData.value = AppState.SuccessState(cachedEventsList.filterToDashboard())
			else statesLiveData.value = AppState.LoadingState
			eventsListJob?.let { return }
			eventsListJob = viewModelCoroutineScope.launch {
				val daysToPutInCache =
					max(settingsData.daysForShowEvents, settingsData.daysForShowEventsWidget)
				if (cachedEventsList.isEmpty()) {
					val cached = cacheRepo.getList()
					if (cached.any())
						statesLiveData.postValue(AppState.SuccessState(cached.filterToDashboard()))
				}

				val result = repo.loadData(
					daysToPutInCache,
					settingsData.isDataContact, settingsData.isDataCalendar
				)
				when (result) {
					is ResourceState.SuccessState -> {
						allEventsFromRepo = result.data
						applyFilterToAllEventsFromRepo(daysToPutInCache)
						cacheRepo.renew(cachedEventsList)
						statesLiveData.postValue(
							AppState.SuccessState(
								cachedEventsList.filterToDashboard()
							)
						)
					}
					is ResourceState.ErrorState -> handleError(result.error)
				}
			}
			eventsListJob?.invokeOnCompletion {
				eventsListJob = null
			}
		} catch (t: Throwable) {
			handleError(t)
		}
	}

	fun addLocalEvent(eventData: EventData) {
		try {
			do {
				eventsListJob?.let { Thread.sleep(1) }
			} while (eventsListJob != null)
			eventsListJob = viewModelCoroutineScope.launch {
				repo.addLocalEvent(eventData)
			}
			eventsListJob?.invokeOnCompletion {
				eventsListJob = null
				loadEvents()
			}
		} catch (t: Throwable) {
			handleError(t)
		}
	}

	fun getDaysToShowEventsCount() = settingsData.daysForShowEvents
	fun getMinutesForStartNotification() = settingsData.minutesForStartNotification
	private fun applyFilterToAllEventsFromRepo(daysToPutInCache: Int) {
		try {
			cachedEventsList.clear()
			val mapToSort = mutableMapOf<LocalDate, MutableList<EventData>>()
			val startDate = LocalDate.now()
			val endDate = startDate.plusDays(daysToPutInCache.toLong())
			allEventsFromRepo.forEach { event ->
				processEvent(event, startDate, endDate, mapToSort)
			}
			mapToSort.toSortedMap().forEach {
				it.value.sortBy(EventData::name)
				it.value.sortBy(EventData::time)
				it.value.sortBy(EventData::timeNotifications)
				cachedEventsList.addAll(it.value)
			}
		} catch (t: Throwable) {
			handleError(t)
		}
	}

	private fun processEvent(
		event: EventData,
		startDate: LocalDate,
		endDate: LocalDate,
		mapToSort: MutableMap<LocalDate, MutableList<EventData>>
	) {
		try {
			if (event.type == EventType.BIRTHDAY)
				fixBirthdayDate(event, startDate, endDate, mapToSort)
			else if (event.date.isAfter(startDate)
				&& event.date.isBefore(endDate)
				|| event.date.isEqual(startDate)
			)
				mapToSort.getOrPut(event.date) { mutableListOf() }.add(event)
		} catch (t: Throwable) {
			handleError(t)
		}
	}

	private fun fixBirthdayDate(
		event: EventData,
		startDate: LocalDate,
		endDate: LocalDate,
		mapToSort: MutableMap<LocalDate, MutableList<EventData>>
	) {
		try {
			event.birthday?.let {
				for (curYear in startDate.year..endDate.year) {
					if (it.safeWithYear(curYear).isAfter(startDate)
						&& it.safeWithYear(curYear).isBefore(endDate)
						|| it.safeWithYear(curYear).isEqual(startDate)
					) {
						mapToSort.getOrPut(
							it.safeWithYear(curYear)
						) { mutableListOf() }.add(
							EventData(
								EventType.BIRTHDAY,
								event.period,
								event.birthday,
								it.safeWithYear(curYear),
								event.time,
								event.timeNotifications,
								event.name,
								event.sourceId,
								event.sourceType
							)
						)
						break
					}
				}
			}
		} catch (t: Throwable) {
			handleError(t)
		}
	}
}