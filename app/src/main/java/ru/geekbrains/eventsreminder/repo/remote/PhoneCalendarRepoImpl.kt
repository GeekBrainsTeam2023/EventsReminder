package ru.geekbrains.eventsreminder.repo.remote

import android.content.ContentUris
import android.content.Context
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.provider.CalendarContract
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.usecases.addEventFromCalendar
import java.time.LocalDate

class PhoneCalendarRepoImpl(val context: Context) :IPhoneCalendarRepo {

    override fun loadEventCalendar(endDay: Int): List<EventData> {
        val listBirthDayEvents = arrayListOf<EventData>()
        val startDay = LocalDate.now()
        val calDate = Calendar.getInstance()
        calDate.timeZone = TimeZone.getDefault()
        calDate.set(startDay.year, startDay.monthValue - 1, startDay.dayOfMonth, 0, 0, 0)
        val start = calDate.timeInMillis
        calDate.add(Calendar.DAY_OF_MONTH,endDay)
        val end = calDate.timeInMillis


        val order = CalendarContract.Instances.BEGIN +" ASC"

        val eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
            .buildUpon()
        ContentUris.appendId(eventsUriBuilder, start)
        ContentUris.appendId(eventsUriBuilder, end)
        val eventsUri = eventsUriBuilder.build()

        val cursor = context.contentResolver.query(
            eventsUri,
            null,
            null,
            null,
            order
        )

        if (cursor != null) while (cursor.moveToNext()) {
            val title = cursor.getStringOrNull(cursor.getColumnIndex(CalendarContract.Instances.TITLE)).orEmpty()
            val start = cursor.getLongOrNull(cursor.getColumnIndex(CalendarContract.Instances.DTSTART)) ?: 0
            listBirthDayEvents.add(addEventFromCalendar(title,start))
        }
        cursor?.close()
        return listBirthDayEvents
    }
}