package ru.geekbrains.eventsreminder.repo.remote

import ru.geekbrains.eventsreminder.domain.EventData

interface IPhoneContactsRepo {
    fun loadBirthDayEvents(): List<EventData>
}