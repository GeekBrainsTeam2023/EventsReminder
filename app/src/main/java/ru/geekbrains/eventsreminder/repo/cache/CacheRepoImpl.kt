package ru.geekbrains.eventsreminder.repo.cache

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventSourceType
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.domain.PeriodType
import ru.geekbrains.eventsreminder.presentation.ui.toInt
import ru.geekbrains.eventsreminder.presentation.ui.toLocalDate
import ru.geekbrains.eventsreminder.presentation.ui.toLocalTime
import javax.inject.Inject

class CacheRepoImpl @Inject constructor(val context: Context): CacheRepo {
    private var mCursor: Cursor? = null
    override fun getList(): List<EventData> {
        mCursor?.close()
        val eventsList = mutableListOf<EventData>()
        mCursor = context.applicationContext.contentResolver.query(
            Contract.PATH_EVENTS_URI,
            null,
            null,
            null,
            Contract._ID + " ASC"
        )
        mCursor?.let { cur ->
            while (cur.moveToNext()) {
                eventsList.add(
                    EventData(
                        EventType.valueOf(cur.getString(cur.getColumnIndexOrThrow(Contract.COL_EVENT_TYPE))),
                        cur.getStringOrNull(cur.getColumnIndexOrThrow(Contract.COL_EVENT_PERIOD))?.let{PeriodType.valueOf(it)},
                        cur.getIntOrNull(cur.getColumnIndexOrThrow(Contract.COL_BIRTHDAY))?.toLocalDate(),
                        cur.getInt(cur.getColumnIndexOrThrow(Contract.COL_EVENT_DATE)).toLocalDate(),
                        cur.getInt(cur.getColumnIndexOrThrow(Contract.COL_EVENT_TIME)).toLocalTime(),
                        cur.getInt(cur.getColumnIndexOrThrow(Contract.COL_TIME_NOTIFICATION)).toLocalTime(),
                        cur.getString(cur.getColumnIndexOrThrow(Contract.COL_EVENT_TITLE)),
                        cur.getLong(cur.getColumnIndexOrThrow(Contract.COL_EVENT_SOURCE_ID)),
                        EventSourceType.valueOf(cur.getString(cur.getColumnIndexOrThrow(Contract.COL_EVENT_SOURCE_TYPE)))
                    )
                )
            }
        }
        return eventsList
    }
    override fun renew(events: List<EventData>){
        context.applicationContext.contentResolver.delete(Contract.PATH_EVENTS_URI,null,null)
        events.forEach{addEventToCache(it)}
    }
    private fun addEventToCache(eventData: EventData) {
        val values = ContentValues()
        values.put(Contract.COL_EVENT_TYPE, eventData.type.toString())
        values.put(Contract.COL_EVENT_PERIOD, eventData.period?.toString())
        values.put(Contract.COL_BIRTHDAY, eventData.birthday?.toInt())
        values.put(Contract.COL_EVENT_DATE, eventData.date.toInt())
        values.put(Contract.COL_EVENT_TIME, eventData.time?.toInt())
        values.put(Contract.COL_TIME_NOTIFICATION, eventData.timeNotifications?.toInt())
        values.put(Contract.COL_EVENT_TITLE, eventData.name)
        values.put(Contract.COL_EVENT_SOURCE_ID, eventData.sourceId)
        values.put(Contract.COL_EVENT_SOURCE_TYPE, eventData.sourceType.toString())
        context.applicationContext.contentResolver.insert(Contract.PATH_EVENTS_URI, values)
    }
}