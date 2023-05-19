package ru.geekbrains.eventsreminder.usecases

import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventSourceType
import ru.geekbrains.eventsreminder.domain.EventType
import java.time.*

fun getLocalDateFromBirthDay(birthDay: String): LocalDate = LocalDate.of(
    LocalDate.now().year,
    LocalDate.parse(birthDay).monthValue,
    LocalDate.parse(birthDay).dayOfMonth
)
fun addBirthDayEventFromContactPhone(name: String, birthDay: String, id: Long): EventData =
    EventData(
        EventType.BIRTHDAY,
        null,
        LocalDate.parse(birthDay),
        getLocalDateFromBirthDay(birthDay),
        null,
        null,
        name,
        id,
        EventSourceType.CONTACTS
    )
fun addBirthDayEventFromLocalEdit(name: String, day: Int, month: Int, year: Int?,minutesBeforeNotification : Int?): EventData =
    EventData(
        EventType.BIRTHDAY,
        null,
        LocalDate.of(year ?: 0, month, day),
        if (LocalDate.now().month.value < month ||
            (LocalDate.now().month.value == month && LocalDate.now().dayOfMonth <= day)
        )
            LocalDate.of(LocalDate.now().year, month, day)
        else
            LocalDate.of(LocalDate.now().year + 1, month, day),
        null,
        minutesBeforeNotification?.let{LocalTime.of(0,minutesBeforeNotification)},
        name,
        0,
        EventSourceType.LOCAL
    )
fun addHolidayEventFromLocalEdit(name: String, day: Int, month: Int,
                                 year: Int,hour : Int?, minute: Int?,
                                 minutesBeforeNotification : Int?): EventData =
    EventData(
        EventType.HOLIDAY,
        null,
        null,
            LocalDate.of(year, month, day),
        hour?.let{minute?.let{LocalTime.of(hour,minute)}},
        minutesBeforeNotification?.let{LocalTime.of(0,minutesBeforeNotification)},
        name,
        0,
        EventSourceType.LOCAL
    )
fun addSimpleEventFromLocalEdit(name: String, day: Int, month: Int,
                                 year: Int,hour : Int?, minute: Int?,
                                minutesBeforeNotification : Int?): EventData =
    EventData(
        EventType.SIMPLE,
        null,
        null,
        LocalDate.of(year, month, day),
        hour?.let{minute?.let{LocalTime.of(hour,minute)}},
        minutesBeforeNotification?.let{LocalTime.of(0,minutesBeforeNotification)},
        name,
        0,
        EventSourceType.LOCAL
    )
fun addEventFromCalendar(name: String, startDate: Long, eventType: EventType, id: Long): EventData {
    val date =
        LocalDateTime.ofInstant(Instant.ofEpochSecond(startDate / 1000), ZoneId.systemDefault())
    return EventData(
        eventType,
        null,
        null,
        date.toLocalDate(),
        date.toLocalTime(),
        LocalTime.parse("00:15:00"),
        name,
        id,
        EventSourceType.CALENDAR
    )
}
fun deleteDuplicateEvents(eventList: MutableList<EventData>): List<EventData> {
    for (i in 0..eventList.size - 2) {
        if (eventList[i].type == EventType.BIRTHDAY) {
            for (j in i + 1..eventList.size - 1) {
                if (eventList[i].type == eventList[j].type) {
                    if ((eventList[i].name.contains(eventList[j].name)) or ((eventList[j].name.contains(
                            eventList[i].name
                        )))
                    ) eventList.removeAt(j)
                }
            }
        }
    }
    return eventList.toList()
}