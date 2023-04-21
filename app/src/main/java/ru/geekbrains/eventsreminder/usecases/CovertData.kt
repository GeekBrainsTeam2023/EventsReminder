package ru.geekbrains.eventsreminder.usecases

import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.domain.PeriodType
import java.time.*

fun getLocalDateFromBirthDay(birthDay: String): LocalDate = LocalDate.of(
    LocalDate.now().year,
    LocalDate.parse(birthDay).monthValue,
    LocalDate.parse(birthDay).dayOfMonth
)


fun addBirthDayEventFromContactPhone(name: String, birthDay: String): EventData = EventData(
    EventType.BIRTHDAY,
    null,
    LocalDate.parse(birthDay),
    getLocalDateFromBirthDay(birthDay),
    LocalTime.parse("00:00:00"),
    LocalTime.parse("00:15:00"),
    name
)


fun addEventFromCalendar(name: String, startDate: Long): EventData {
    val date =
        LocalDateTime.ofInstant(Instant.ofEpochSecond(startDate / 1000), ZoneId.systemDefault())
    return EventData(
        EventType.SIMPLE,
        null,
        null,
        date.toLocalDate(),
        date.toLocalTime(),
        LocalTime.parse("00:15:00"),
        name
    )
}