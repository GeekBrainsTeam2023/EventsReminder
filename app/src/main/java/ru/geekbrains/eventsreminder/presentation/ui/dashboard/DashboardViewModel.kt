package ru.geekbrains.eventsreminder.presentation.ui.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.geekbrains.eventsreminder.di.RepoFactory
import ru.geekbrains.eventsreminder.di.SettingsDataFactory
import ru.geekbrains.eventsreminder.domain.AppState
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.ResourceState
import java.time.LocalDate


class DashboardViewModel() : ViewModel(), LifecycleObserver {
    private val settingsData = SettingsDataFactory.getSettingsData()
    private val repo = RepoFactory.getRepo()
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

    //    private val _text = MutableLiveData<String>().apply {
//        value = "Здесь показывается интервал для отображения списка событий"
//    }
//    val text: LiveData<String> = _text
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadEvents(){
        try{
        loadEventsListJob?.let{return}
        loadEventsListJob = viewmodelCoroutineScope.launch {
           val result = repo.loadData(settingsData.daysForShowEvents,settingsData.isDataContact,settingsData.isDataCalendar)
            when(result){
                is ResourceState.SuccessState -> {
                    allEvents = result.data
                    appendFilter()
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun appendFilter(){
        try {
            filteredEventsList.clear()
            val mapToSort = mutableMapOf<LocalDate,MutableList<EventData>>()
            val startDate = LocalDate.now()
            var date = startDate
            do {
                val currentDayEvents = allEvents.filter {
                    it.date.dayOfMonth == date.dayOfMonth
                            && it.date.month == date.month
                            && it.date.year == date.year
                            || (it.birthday?.dayOfMonth == date.dayOfMonth
                                && it.birthday.month == date.month)
                }
                if (currentDayEvents.any()) mapToSort.put(date,currentDayEvents.toMutableList())
                date = date.plusDays(1L)
            }while(date < startDate.plusDays(settingsData.daysForShowEvents.toLong()))
            // TODO: Optimisation is needed
            mapToSort.values.forEach {
                it.sortBy ( EventData::name)
                it.sortBy ( EventData::time)
                it.sortBy ( EventData::timeNotifications )
                }
            mapToSort.forEach{filteredEventsList.addAll(it.value)}

        } catch (t: Throwable) {
            handleError(t)
        }
    }
}