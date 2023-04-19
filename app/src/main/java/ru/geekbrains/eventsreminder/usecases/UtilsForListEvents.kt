package ru.geekbrains.eventsreminder.usecases

import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.SettingsData
import java.time.LocalDate

fun getActualListEvents(listEvents: List<EventData>, settings: SettingsData): List<EventData> {
    val actualListEvents = mutableListOf<EventData>()
    val date = LocalDate.now()
    for (event in listEvents) {
        if (event.date <= date.plusDays(settings.daysForShowEvents.toLong()))
            actualListEvents.add(event)

        if (event.date.plusDays(
                event.period?.toLong() ?: 0
            ) <= date.plusDays(settings.daysForShowEvents.toLong())
        )
            actualListEvents.add(event)
    }
    return actualListEvents
}