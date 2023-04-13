package ru.geekbrains.eventsreminder.repo.remote

import ru.geekbrains.eventsreminder.domain.EventData

interface IPhoneCalendarRepo {
    fun loadEventCalendar(endDay:Int): List<EventData>
}