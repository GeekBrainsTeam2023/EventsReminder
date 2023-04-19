package ru.geekbrains.eventsreminder.presentation.ui.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.geekbrains.eventsreminder.di.RepoFactory
import ru.geekbrains.eventsreminder.di.SettingsDataFactory
import ru.geekbrains.eventsreminder.domain.AppState
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.ResourceState
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.repo.Repo
import ru.geekbrains.eventsreminder.repo.local.LocalRepo
import java.time.LocalDate


class DashboardViewModel() : ViewModel(), LifecycleObserver {
    private val settingsData = SettingsDataFactory.getSettingsData()
    private val repo = RepoFactory.getRepo()
    private val statesLiveData: MutableLiveData<AppState> = MutableLiveData()
    private var allEvents = listOf<EventData>()
    private val filteredEventsList = mutableMapOf<LocalDate,List<EventData>>()
    private val viewmodelCoroutineScope = CoroutineScope(
        Dispatchers.IO
                + SupervisorJob()
                + CoroutineExceptionHandler { _, throwable -> handleError(throwable) }
    )
    var loadEventsListJob: Job? = null


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
           val result = repo.loadData()//settingsData.isDataCalendar,settingsData.isDataContact,settingsData.daysForShowEvents)
            when(result){
                is ResourceState.SuccessState -> {
                    allEvents = result.data
                    appendFilter()
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
                filteredEventsList.put(date,currentDayEvents)
                date = date.plusDays(1L)
            }while(date < startDate.plusDays(settingsData.daysForShowEvents.toLong()))
            statesLiveData.postValue(AppState.SuccessState(filteredEventsList))
        } catch (t: Throwable) {
            handleError(t)
        }
    }

}