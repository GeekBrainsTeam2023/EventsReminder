package ru.geekbrains.eventsreminder.widget

import android.net.Uri
import android.provider.BaseColumns


object Contract : BaseColumns {
    const val _ID = "id"
    const val TABLE_NAME = "events_list"
    const val COL_EVENT_TYPE = "event_type"
    const val COL_EVENT_TITLE = "event_title"
    const val COL_EVENT_DATE = "event_date"
    const val COL_EVENT_TIME = "event_time"
    const val SCHEMA = "content://"
    const val AUTHORITY = "ru.geekbrains.eventsreminder.widget"
    val BASE_CONTENT_URI: Uri = Uri.parse(SCHEMA + AUTHORITY)
    const val PATH_EVENTS = "events"
    val PATH_EVENTS_URI: Uri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENTS).build()
}