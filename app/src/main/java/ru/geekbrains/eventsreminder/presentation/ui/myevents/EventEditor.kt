package ru.geekbrains.eventsreminder.presentation.ui.myevents

import ru.geekbrains.eventsreminder.domain.EventData

interface EventEditor {
    fun openEditEvent(event: EventData)
    fun openRemoveEvent(event: EventData)
}