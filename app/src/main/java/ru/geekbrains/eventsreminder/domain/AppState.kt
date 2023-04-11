package ru.geekbrains.eventsreminder.domain

sealed class AppState{
    data class SuccessEventsState(val eventList: List<EventData>) : AppState()
    data class SuccessNotificationsState(val notificationList: List<String>) : AppState()
    object LoadingState : AppState()
    data class ErrorState(val error: Throwable) : AppState()
}
