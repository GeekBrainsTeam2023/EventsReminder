package ru.geekbrains.eventsreminder.usecases

import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventNotificationData
import ru.geekbrains.eventsreminder.domain.EventSourceType
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.presentation.ui.MAX_YEAR
import ru.geekbrains.eventsreminder.presentation.ui.safeWithYear
import java.time.*

fun getCelebrationDateForBirthDay(birthDay: LocalDate) =
	with(LocalDate.now()) {
		if (birthDay.safeWithYear(year) < this)
			LocalDate.of(year + 1, birthDay.month, birthDay.dayOfMonth)
		else
			LocalDate.of(year, birthDay.month, birthDay.dayOfMonth)
	}

fun extractBirthday(text: String): LocalDate {
	//2017-05-23
	// "--05-27" - пример строки дня рождения без года
	val numbers = text.split('-', '/', '.').filter { s -> s != "" }
	return LocalDate.of(
		if (numbers.size == 3)
			numbers[0].toInt()
		else MAX_YEAR,
		numbers[numbers.size - 2].toInt(),
		numbers[numbers.size - 1].toInt()
	)
}

fun addBirthDayEventFromContactPhone(name: String, birthDay: LocalDate, id: Long): EventData {
	return EventData(
		EventType.BIRTHDAY,
		null,
		birthDay,
		getCelebrationDateForBirthDay(birthDay),
		null,
		null,
		name,
		id,
		EventSourceType.CONTACTS
	)
}

fun addBirthDayEventFromLocalEdit(
	name: String, day: Int, month: Int, year: Int?,
	minutesBeforeNotification: Int?,
	sourceId: Long = 0
): EventData =
	with(LocalDate.of(year ?: MAX_YEAR, month, day)) {
		EventData(
			EventType.BIRTHDAY,
			null,
			this,
			getCelebrationDateForBirthDay(this),
			null,
			minutesBeforeNotification?.let { LocalTime.of(0, minutesBeforeNotification) },
			name,
			sourceId,
			EventSourceType.LOCAL
		)
	}

fun addHolidayEventFromLocalEdit(
	name: String, day: Int, month: Int,
	year: Int, hour: Int?, minute: Int?,
	minutesBeforeNotification: Int?,
	sourceId: Long = 0
): EventData =
	EventData(
		EventType.HOLIDAY,
		null,
		null,
		LocalDate.of(year, month, day),
		hour?.let { minute?.let { LocalTime.of(hour, minute) } },
		minutesBeforeNotification?.let { LocalTime.of(0, minutesBeforeNotification) },
		name,
		sourceId,
		EventSourceType.LOCAL
	)

fun addSimpleEventFromLocalEdit(
	name: String, day: Int, month: Int,
	year: Int, hour: Int?, minute: Int?,
	minutesBeforeNotification: Int?,
	sourceId: Long = 0
): EventData =
	EventData(
		EventType.SIMPLE,
		null,
		null,
		LocalDate.of(year, month, day),
		hour?.let { minute?.let { LocalTime.of(hour, minute) } },
		minutesBeforeNotification?.let { LocalTime.of(0, minutesBeforeNotification) },
		name,
		sourceId,
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

fun isNewEvent(
	eventNotificationList: MutableList<EventNotificationData>,
	event: EventData
): Boolean {
	for (eventNotifi in eventNotificationList) {
		if ((eventNotifi.date == event.date) && (eventNotifi.time == event.time) && (eventNotifi.name == event.name)) return false
	}
	return true
}

fun addNotificationEventFromEvent(event: EventData): EventNotificationData =
	EventNotificationData(
		null,
		event.type,
		event.period,
		event.birthday,
		event.date,
		event.time,
		event.timeNotifications,
		event.name,
		event.sourceId,
		event.sourceType
	)

fun addEventsListToNotificationEventsList(
	eventNotificationList: MutableList<EventNotificationData>,
	eventList: MutableList<EventData>
): MutableList<EventNotificationData> {
	for (event in eventList) {
		if (isNewEvent(eventNotificationList, event)) {
			eventNotificationList.add(addNotificationEventFromEvent(event))
		}
	}
	return eventNotificationList.toMutableList()
}