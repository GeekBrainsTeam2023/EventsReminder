package ru.geekbrains.eventsreminder.widget


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.util.Log
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.database.getIntOrNull
import androidx.preference.PreferenceManager
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.domain.EventSourceType
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.presentation.ui.toAgeInWordsByDate
import ru.geekbrains.eventsreminder.presentation.ui.toInt
import ru.geekbrains.eventsreminder.presentation.ui.toLocalDate
import ru.geekbrains.eventsreminder.presentation.ui.toLocalTime
import ru.geekbrains.eventsreminder.repo.cache.Contract
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class WidgetRemoteViewsFactory
@Inject
constructor(
    private val applicationContext: Context,
) :
    RemoteViewsService.RemoteViewsFactory {
    private var mCursor: Cursor? = null

    companion object {
        const val TAG = "ru.geekbrains.eventsreminder.widget.WidgetRemoteViewsFactory"
    }

    override fun onCreate() {}
    override fun onDataSetChanged() {
        try {
            val prefs: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val interval = prefs.getInt(
                applicationContext.getString(R.string.key_widget_interval_of_events_preference),
                365
            )
            mCursor?.close()
            val identityToken = Binder.clearCallingIdentity()
            val uri: Uri = Contract.PATH_EVENTS_URI
            val fromDate = LocalDate.now().toInt()
            val toDate = LocalDate.now().plusDays(interval.toLong()).toInt()

            mCursor = applicationContext.contentResolver.query(
                uri,
                null,
                Contract.COL_EVENT_DATE + " BETWEEN ? AND ?",
                arrayOf(fromDate.toString(),toDate.toString()),
                Contract.COL_EVENT_DATE + " ASC"
            )
            Binder.restoreCallingIdentity(identityToken)
        } catch (t: Throwable) {
            Log.e(TAG, null, t)
            throw t
        }
    }

    override fun onDestroy() {
        try {
            mCursor?.close()
        } catch (t: Throwable) {
            Log.e(TAG, null, t)
            throw t
        }
    }

    override fun getCount(): Int {
        return mCursor?.count ?: 0
    }

    override fun getViewAt(position: Int): RemoteViews? {
        try {
            if (position == AdapterView.INVALID_POSITION ||
                mCursor?.moveToPosition(position) != true
            ) return null
            val rv = RemoteViews(applicationContext.packageName, R.layout.item_app_widget)
            /**
             * Для работы кликов по элементам списка в AppWidget.onUpdate()
             * требуется установить темплейт виджета
             * widgetView.setPendingIntentTemplate(R.id.widgetList, clickPendingIntentTemplate)
             */
            rv.setOnClickFillInIntent(
                R.id.itemAppWidget,
                Intent()
            )// Здесь достаточно пустого интента
            val prefs: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val isShowingEventDate = prefs.getBoolean(
                applicationContext.getString(R.string.key_event_date_checkbox_preference),
                true
            )
            val isShowingEventTime = prefs.getBoolean(
                applicationContext.getString(R.string.key_event_time_checkbox_preference),
                true
            )
            val isShowingAge = prefs.getBoolean(
                applicationContext.getString(R.string.key_age_checkbox_preference),
                true
            )
            val sizeFontWidget = prefs.getInt(
                applicationContext.getString(R.string.key_widget_font_size_preference), 13
            ).toFloat()
            val backColor = prefs.getInt(
                applicationContext.getString(R.string.key_background_color_preference), 0xE8E6EC
            )
            val altBackColor = prefs.getInt(
                applicationContext.getString(R.string.key_background_alternating_color_preference),
                0xDCF3F3
            )
            val birthdayTextColor = prefs.getInt(
                applicationContext.getString(R.string.key_widget_birthday_font_color_preference),
                0x01579B
            )
            val holidayTextColor = prefs.getInt(
                applicationContext.getString(R.string.key_widget_holiday_font_color_preference),
                0x3700B3
            )
            val simpleEventTextColor = prefs.getInt(
                applicationContext.getString(R.string.key_widget_simple_event_font_color_preference),
                0x151414
            )
            if (position % 2 == 0) {
                rv.setInt(R.id.itemAppWidget, "setBackgroundColor", backColor)
            } else {
                rv.setInt(R.id.itemAppWidget, "setBackgroundColor", altBackColor)
            }
            mCursor?.let {
                val eventSource =
                    it.getString(it.getColumnIndexOrThrow(Contract.COL_EVENT_SOURCE_TYPE))
                cleanupTextViews(rv)
                when (it.getString(it.getColumnIndexOrThrow(Contract.COL_EVENT_TYPE))) {
                    EventType.SIMPLE.toString() -> {
                        setWidgetLineParameters(
                            rv,
                            simpleEventTextColor,
                            sizeFontWidget
                        )
                        bindTimeWithEventTimeTextView(isShowingEventTime, rv, it)
                    }

                    EventType.HOLIDAY.toString() -> {
                        setWidgetLineParameters(
                            rv,
                            holidayTextColor,
                            sizeFontWidget
                        )
                        bindTimeWithEventTimeTextView(
                            isShowingEventTime &&
                                    eventSource == EventSourceType.LOCAL.toString(), rv, it
                        )
                    }

                    EventType.BIRTHDAY.toString() -> {
                        setWidgetLineParameters(
                            rv,
                            birthdayTextColor,
                            sizeFontWidget
                        )
                        bindAgeWithWidgetAgeTextView(isShowingAge, rv, it)
                    }
                }
                rv.setTextViewText(
                    R.id.widgetEventTitleTextview,
                    it.getString(it.getColumnIndexOrThrow(Contract.COL_EVENT_TITLE))
                )
                if (isShowingEventDate) {
                    with(it.getInt(it.getColumnIndexOrThrow(Contract.COL_EVENT_DATE))) {
                        val date = String.format("(%02d", this % 100) + "." +
                                String.format("%02d", this / 100 % 100) + "." +
                                (this / 10000) + ")"
                        rv.setTextViewText(R.id.widgetEventDateTextview, date)
                        rv.setViewVisibility(R.id.widgetEventDateTextview, View.VISIBLE)
                    }
                } else {
                    rv.setViewVisibility(R.id.widgetEventDateTextview, View.GONE)
                }
            }
            return rv
        } catch (t: Throwable) {
            Log.e(TAG, null, t)
            throw t
        }
    }

    private fun cleanupTextViews(rv: RemoteViews) {
        rv.setViewVisibility(R.id.widgetAgeTextview, View.GONE)
        rv.setTextViewText(R.id.widgetAgeTextview, "")
        rv.setViewVisibility(R.id.widgetEventTimeTextview, View.GONE)
        rv.setTextViewText(R.id.widgetEventTimeTextview, "")
    }

    private fun bindAgeWithWidgetAgeTextView(
        isShowingAge: Boolean,
        rv: RemoteViews,
        it: Cursor
    ) {
        try {
            if (isShowingAge) {
                it.getIntOrNull(it.getColumnIndexOrThrow(Contract.COL_BIRTHDAY))?.let { birthday ->
                    val date = it.getInt(it.getColumnIndexOrThrow(Contract.COL_EVENT_DATE))
                        .toLocalDate()
                    val birthdate = birthday.toLocalDate()
                    if (date >= birthdate) {
                        val age = buildString {
                            append("(")
                            append(
                                birthdate
                                    .toAgeInWordsByDate(date)
                            )
                            append(")")
                        }
                        rv.setTextViewText(R.id.widgetAgeTextview, age)
                        rv.setViewVisibility(R.id.widgetAgeTextview, View.VISIBLE)
                        return
                    }
                }
            } else {
                rv.setViewVisibility(R.id.widgetAgeTextview, View.GONE)
                rv.setTextViewText(R.id.widgetAgeTextview, "")
            }
        } catch (t: Throwable) {
            Log.e(TAG, null, t)
            throw t
        }
    }

    private fun bindTimeWithEventTimeTextView(
        isShowingEventTime: Boolean,
        rv: RemoteViews,
        it: Cursor
    ) {
        try {
            val time = it.getIntOrNull(it.getColumnIndexOrThrow(Contract.COL_EVENT_TIME))
            if (isShowingEventTime && time != null) {
                rv.setTextViewText(
                    R.id.widgetEventTimeTextview,
                    time.toLocalTime().format(
                        DateTimeFormatter.ofPattern("HH:mm")
                    )
                )
                rv.setViewVisibility(R.id.widgetEventTimeTextview, View.VISIBLE)
            } else rv.setViewVisibility(R.id.widgetEventTimeTextview, View.GONE)
        } catch (t: Throwable) {
            Log.e(TAG, null, t)
            throw t
        }
    }

    private fun setWidgetLineParameters(rv: RemoteViews, color: Int, sizeFontWidget: Float) {
        try {
            with(rv) {
                setTextColor(R.id.widgetEventTitleTextview, color)
                setTextColor(R.id.widgetEventDateTextview, color)
                setTextColor(R.id.widgetEventTimeTextview, color)
                setTextColor(R.id.widgetAgeTextview, color)
                setTextViewTextSize(
                    R.id.widgetEventTitleTextview, COMPLEX_UNIT_SP,
                    sizeFontWidget
                )
                setTextViewTextSize(
                    R.id.widgetEventDateTextview, COMPLEX_UNIT_SP,
                    sizeFontWidget
                )
                setTextViewTextSize(
                    R.id.widgetEventTimeTextview, COMPLEX_UNIT_SP,
                    sizeFontWidget
                )
                setTextViewTextSize(
                    R.id.widgetAgeTextview, COMPLEX_UNIT_SP,
                    sizeFontWidget
                )
            }
        } catch (t: Throwable) {
            Log.e(TAG, null, t)
            throw t
        }
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        try {
            mCursor?.let {
                if (it.moveToPosition(position)) return it.getLong(0)
            }
            return position.toLong()
        } catch (t: Throwable) {
            logErr(t)
            throw t
        }
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    private fun logErr(t: Throwable) = logErr(t, this::class.java.toString())

    private fun logErr(t: Throwable, TAG: String) {
        try {
            Log.e(TAG, "", t)
        } catch (_: Throwable) {
        }
    }
}