package ru.geekbrains.eventsreminder.repo.remote

import android.content.ContentUris
import android.content.Context
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.net.Uri
import android.provider.CalendarContract
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.usecases.addEventFromCalendar
import java.time.LocalDate
import javax.inject.Inject

class PhoneCalendarRepoImpl @Inject constructor(
	val context: Context
) : PhoneCalendarRepo {
	override fun loadEventCalendar(daysToSeek: Int): List<EventData> {
		val listBirthDayEvents = arrayListOf<EventData>()
		try {
			val startDay = LocalDate.now()
			val calendarTime = Calendar.getInstance().apply { timeZone = TimeZone.getDefault() }.timeInMillis
			val startTick = calendarTime - DateUtils.DAY_IN_MILLIS
			val endTick = calendarTime + DateUtils.DAY_IN_MILLIS * daysToSeek
			val uriBuilder = CalendarContract.Instances.CONTENT_URI
				.buildUpon().apply {
					ContentUris.appendId(this, startTick)
					ContentUris.appendId(this, endTick)
				}

			context.contentResolver.query(
				uriBuilder.build(),
				null,
				null,
				null,
				CalendarContract.Instances.BEGIN + " ASC"
			)?.run {
				while (moveToNext()) {
					val title = getStringOrNull(getColumnIndex(CalendarContract.Instances.TITLE)).orEmpty()
					val startTime = getLongOrNull(getColumnIndex(CalendarContract.Instances.DTSTART)) ?: 0
					val description = getStringOrNull(getColumnIndex(CalendarContract.Instances.DESCRIPTION)).orEmpty()
					var eventType = EventType.SIMPLE
					val id = getLongOrNull(getColumnIndex(CalendarContract.Instances.EVENT_ID)) ?: 0
					if (description.isNotEmpty()) {
						eventType =
							if (description.contains(context.getString(R.string.description_contains_birthday_repoimpl)) ||
								description.contains(context.getString(R.string.description_contains_day_of_birth_repoimpl))
							)
								EventType.BIRTHDAY
							else EventType.HOLIDAY
					}
					listBirthDayEvents.add(addEventFromCalendar(title, startTime, eventType, id))
				}
				close()
			}
		} catch (t: Throwable) {
			logAndToast(t)
		}
		return listBirthDayEvents
	}

	private fun logAndToast(t: Throwable) = logAndToast(t, this::class.java.toString())

	private fun logAndToast(t: Throwable, TAG: String) {
		try {
			Log.e(TAG, "", t)
			Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show()
		} catch (_: Throwable) {
		}
	}
}