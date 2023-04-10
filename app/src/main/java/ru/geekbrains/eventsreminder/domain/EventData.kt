package ru.geekbrains.eventsreminder.domain

import java.time.LocalDate
import java.time.LocalTime

data class EventData(
    val type: EventType,
    val period: PeriodType,
    val birthday:LocalDate,
    val date: LocalDate,
    val time: LocalTime,
    val timeNotifications:LocalTime,
    val name:String
)
