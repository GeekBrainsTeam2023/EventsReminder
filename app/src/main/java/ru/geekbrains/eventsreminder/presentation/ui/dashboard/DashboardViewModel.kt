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
import ru.geekbrains.eventsreminder.repo.Repo
import ru.geekbrains.eventsreminder.repo.cache.CacheRepo
import java.time.LocalDate
import javax.inject.Inject


class DashboardViewModel @Inject constructor(
    val settingsData: SettingsData,
    val repo: Repo,
    val cache: CacheRepo
) : ViewModel(), LifecycleObserver {
    val statesLiveData: MutableLiveData<AppState> = MutableLiveData()
    private var allEvents = listOf<EventData>()
    private val filteredEventsList = mutableListOf<EventData>()
    private val viewmodelCoroutineScope = CoroutineScope(
        Dispatchers.IO
                + SupervisorJob()
                + CoroutineExceptionHandler { _, throwable -> handleError(throwable) }
    )
    var loadEventsListJob: Job? = null
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
            viewmodelCoroutineScope.cancel()
        } catch (t: Throwable) {
            handleError(t)
        }
    }
    fun loadEvents() {
        try {
            if (filteredEventsList.any())
                statesLiveData.postValue(AppState.SuccessState(filteredEventsList.toList()))
            else statesLiveData.postValue(AppState.LoadingState)
            loadEventsListJob?.let { return }
            loadEventsListJob = viewmodelCoroutineScope.launch {
                if (filteredEventsList.isEmpty()){
                    val cached = cache.getList()
                    if (cached.any()) statesLiveData.postValue(AppState.SuccessState(cached))
                }
                val result = repo.loadData(
                    settingsData.daysForShowEvents,
                    settingsData.isDataContact, settingsData.isDataCalendar
                )
                when (result) {
                    is ResourceState.SuccessState -> {
                        allEvents = result.data
                        appendFilter()
                        cache.renew(filteredEventsList)
                        statesLiveData.postValue(AppState.SuccessState(filteredEventsList.toList()))
                    }
                    is ResourceState.ErrorState -> handleError(result.error)
                }
            }
            loadEventsListJob?.invokeOnCompletion {
                loadEventsListJob = null
            }
        } catch (t: Throwable) {
            handleError(t)
        }
    }
    fun getDaysToShowEventsCount() = settingsData.daysForShowEvents
    fun appendFilter() {
        try {
            filteredEventsList.clear()
            val mapToSort = mutableMapOf<LocalDate, MutableList<EventData>>()
            val startDate = LocalDate.now()
            val endDate = startDate.plusDays(settingsData.daysForShowEvents.toLong())
            allEvents.forEach { event ->
                if (event.type == EventType.BIRTHDAY)
                    event.birthday?.let {
                        for (curYear in startDate.year..endDate.year) {
                            if (it.withYear(curYear).isAfter(startDate)
                                && it.withYear(curYear).isBefore(endDate)
                                || it.withYear(curYear).isEqual(startDate)
                            ) {
                               mapToSort.getOrPut(it.withYear(curYear)
                               ) { mutableListOf() }.add(
                                    EventData(
                                        EventType.BIRTHDAY,
                                        event.period,
                                        event.birthday,
                                        it.withYear(curYear),
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
                else if (event.date.isAfter(startDate)
                    && event.date.isBefore(endDate)
                    || event.date.isEqual(startDate)
                )
                    mapToSort.getOrPut(event.date, { mutableListOf() }).add(event)
            }
            mapToSort.toSortedMap().forEach {
                it.value.sortBy(EventData::name)
                it.value.sortBy(EventData::time)
                it.value.sortBy(EventData::timeNotifications)
                filteredEventsList.addAll(it.value)
            }
        } catch (t: Throwable) {
            handleError(t)
        }
    }
}