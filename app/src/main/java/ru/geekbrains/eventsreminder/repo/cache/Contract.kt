package ru.geekbrains.eventsreminder.repo.cache

import android.net.Uri
import android.provider.BaseColumns


object Contract : BaseColumns {
    const val _ID = "id"
    const val TABLE_NAME = "events_list_cache"
    const val COL_EVENT_TYPE = "event_type"
    const val COL_EVENT_PERIOD = "event_period"
    const val COL_BIRTHDAY = "event_birthday"
    const val COL_EVENT_DATE = "event_date"
    const val COL_EVENT_TIME = "event_time"
    const val COL_TIME_NOTIFICATION = "event_time_notification"
    const val COL_EVENT_TITLE = "event_title"
    //const val COL_EVENT_SOURCE = "event_source"

    const val SCHEMA = "content://"
    const val AUTHORITY = "ru.geekbrains.eventsreminder.widget"
    val BASE_CONTENT_URI: Uri = Uri.parse(SCHEMA + AUTHORITY)
    const val PATH_EVENTS = TABLE_NAME
    val PATH_EVENTS_URI: Uri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENTS).build()
}