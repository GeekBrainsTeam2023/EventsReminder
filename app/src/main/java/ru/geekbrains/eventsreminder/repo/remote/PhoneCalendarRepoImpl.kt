package ru.geekbrains.eventsreminder.repo.remote

import android.content.ContentUris
import android.content.Context
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.provider.CalendarContract
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.usecases.addEventFromCalendar
import java.time.LocalDate
import javax.inject.Inject

class PhoneCalendarRepoImpl @Inject constructor (
	val context: Context) : IPhoneCalendarRepo {
	override fun loadEventCalendar(endDay: Int): List<EventData> {
		val listBirthDayEvents = arrayListOf<EventData>()
		val startDay = LocalDate.now()
		val calDate = Calendar.getInstance()
		calDate.timeZone = TimeZone.getDefault()
		calDate.set(startDay.year, startDay.monthValue - 1, startDay.dayOfMonth, 0, 0, 0)
		val start = calDate.timeInMillis
		calDate.add(Calendar.DAY_OF_MONTH, endDay)
		val end = calDate.timeInMillis
		val order = CalendarContract.Instances.BEGIN + " ASC"
		val eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
			.buildUpon()
		ContentUris.appendId(eventsUriBuilder, start)
		ContentUris.appendId(eventsUriBuilder, end)
		val eventsUri = eventsUriBuilder.build()

		context.contentResolver.query(
			eventsUri,
			null,
			null,
			null,
			order
		)?.run {
			while (moveToNext()) {
				val title =
					getStringOrNull(getColumnIndex(CalendarContract.Instances.TITLE)).orEmpty()
				val start = getLongOrNull(getColumnIndex(CalendarContract.Instances.DTSTART)) ?: 0
				val description = getStringOrNull(getColumnIndex(CalendarContract.Instances.DESCRIPTION)).orEmpty()
				var eventType = EventType.SIMPLE
				val id = getLongOrNull(getColumnIndex(CalendarContract.Instances.EVENT_ID)) ?: 0
				if (description.isNotEmpty()) {
					if (description.contains(context.getString(R.string.description_contains_birthday_repoimpl)) ||
						description.contains(context.getString(R.string.description_contains_day_of_birth_repoimpl)))
						eventType=EventType.BIRTHDAY
					 else eventType=EventType.HOLIDAY
				}
				listBirthDayEvents.add(addEventFromCalendar(title, start,eventType,id))
			}
			close()
		}
		return listBirthDayEvents
	}
}