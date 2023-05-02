package ru.geekbrains.eventsreminder.usecases

import android.util.Log
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


fun addEventFromCalendar(name: String, startDate: Long, eventType: EventType): EventData {
    val date =
        LocalDateTime.ofInstant(Instant.ofEpochSecond(startDate / 1000), ZoneId.systemDefault())

    return EventData(
        eventType,
        null,
        null,
        date.toLocalDate(),
        date.toLocalTime(),
        LocalTime.parse("00:15:00"),
        name
    )
}

fun deleteDuplicateEvents(eventList:MutableList<EventData>):List<EventData>{
    for(i in 0..eventList.size-2){
       if (eventList[i].type == EventType.BIRTHDAY) {
           for (j in i+1..eventList.size-1){
               if (eventList[i].type == eventList[j].type) {
                   if ((eventList[i].name.contains(eventList[j].name)) or ((eventList[j].name.contains(eventList[i].name)))) eventList.removeAt(j)
               }
           }
       }
    }
    return eventList.toList()
}