package ru.geekbrains.eventsreminder.presentation.ui.myevents

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.geekbrains.eventsreminder.domain.AppState
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.domain.ResourceState
import ru.geekbrains.eventsreminder.repo.Repo
import ru.geekbrains.eventsreminder.repo.cache.CacheRepo
import java.time.LocalDate
import javax.inject.Inject

class MyEventsViewModel @Inject constructor(
    val repo: Repo,
    val cache: CacheRepo,
) : ViewModel(), LifecycleObserver {
    val statesLiveData: MutableLiveData<AppState> = MutableLiveData()
    val cachedLocalEvents = mutableListOf<EventData>()
    private val viewModelCoroutineScope = CoroutineScope(
        Dispatchers.IO
                + SupervisorJob()
                + CoroutineExceptionHandler { _, throwable -> handleError(throwable) }
    )
    var localEventsJob: Job? = null

    /**
     * Хранимые события для работы diffUtils
     * */
    val storedEvents = mutableListOf<EventData>()
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

    fun loadMyEvents() {
        try {
            if (cachedLocalEvents.any())
                statesLiveData.postValue(AppState.SuccessState(cachedLocalEvents.toList()))
            else statesLiveData.postValue(AppState.LoadingState)
            localEventsJob?.let { return }
            localEventsJob = viewModelCoroutineScope.launch {
                val result = repo.loadLocalData()
                when (result) {
                    is ResourceState.SuccessState -> {
                        addToCache(result.data)
                        statesLiveData.postValue(AppState.SuccessState(cachedLocalEvents.toList()))
                    }

                    is ResourceState.ErrorState -> handleError(result.error)
                }
            }
            localEventsJob?.invokeOnCompletion {
                localEventsJob = null
            }
        } catch (t: Throwable) {
            handleError(t)
        }
    }

    private fun addToCache(events: List<EventData>) {
        try {
            cachedLocalEvents.clear()
            arrangeByDatesToSortedMap(events).forEach {
                it.value.sortBy(EventData::name)
                it.value.sortBy(EventData::time)
                it.value.sortBy(EventData::timeNotifications)
                cachedLocalEvents.addAll(it.value)
            }
        } catch (t: Throwable) {
            handleError(t)
        }
    }

    private fun arrangeByDatesToSortedMap(events: List<EventData>): Map<LocalDate, MutableList<EventData>> {
        val mapToSort = mutableMapOf<LocalDate, MutableList<EventData>>()
        try {
            val startDate = LocalDate.now()
            val endDate = startDate.plusDays(365L)
            events.forEach { event ->
                if (event.type == EventType.BIRTHDAY)
                    event.birthday?.let {
                        for (curYear in startDate.year..endDate.year) {
                            if (it.withYear(curYear).isAfter(startDate)
                                && it.withYear(curYear).isBefore(endDate)
                                || it.withYear(curYear).isEqual(startDate)
                            ) {
                                mapToSort.getOrPut(
                                    it.withYear(curYear)
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
                else mapToSort.getOrPut(event.date) { mutableListOf() }.add(event)
            }
        } catch (t: Throwable) {
            handleError(t)
        }
        return mapToSort
    }

    fun deleteMyEvent(event: EventData) {
        try {
            localEventsJob?.let { return }
            localEventsJob = viewModelCoroutineScope.launch {
                repo.deleteLocalEvent(event)
                cachedLocalEvents.remove(event)
                statesLiveData.postValue(AppState.SuccessState(cachedLocalEvents.toList()))
            }
            localEventsJob?.invokeOnCompletion {
                localEventsJob = null
            }
        } catch (t: Throwable) {
            handleError(t)
        }
    }

    fun clearAllLocalEvents() {
        try {
            localEventsJob?.let { return }
            localEventsJob = viewModelCoroutineScope.launch {
                repo.clearAllLocalEvents()
                cachedLocalEvents.clear()
                statesLiveData.postValue(AppState.SuccessState(cachedLocalEvents.toList()))
            }
            localEventsJob?.invokeOnCompletion {
                localEventsJob = null
            }
        } catch (t: Throwable) {
            handleError(t)
        }
    }
}