package ru.geekbrains.eventsreminder.widget


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.preference.PreferenceManager
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.domain.EventType
import java.time.LocalDate
import javax.inject.Inject

class WidgetRemoteViewsFactory
@Inject
constructor(
    private val applicationContext: Context,
) :
    RemoteViewsService.RemoteViewsFactory {
    private var mCursor: Cursor? = null
    override fun onCreate() {}
    override fun onDataSetChanged() {

        // Получаем настройки из приложения
        val prefs: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val interval = prefs.getInt(
            applicationContext.getString(R.string.key_widget_interval_of_events_preference),
            365
        )

        mCursor?.close()
        val identityToken = Binder.clearCallingIdentity()
        val uri: Uri = Contract.PATH_EVENTS_URI

        val toDate = with(LocalDate.now().plusDays(interval.toLong()))
        {year*10000+month.value*100+dayOfMonth}
        mCursor = applicationContext.contentResolver.query(
            uri,
            null,
            Contract.COL_EVENT_DATE+" < ?",
            arrayOf(toDate.toString()),
            Contract._ID + " ASC"
        )
        //db.rawQuery("SELECT Description FROM Table_Name WHERE Num BETWEEN "+(inputNumber-Range)+" AND "+(inputNumber+Range) +"ORDER BY ABS(Num- "+inputNumber+")", null)
        Binder.restoreCallingIdentity(identityToken)
    }

    override fun onDestroy() {
        mCursor?.close()
    }

    override fun getCount(): Int {
        return mCursor?.count ?: 0
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position == AdapterView.INVALID_POSITION || mCursor?.moveToPosition(
                position
            ) != true
        ) {
            return null
        }

        val rv = RemoteViews(applicationContext.packageName, R.layout.item_app_widget)
        // Для работы кликов по элементам списка в AppWidget.onUpdate()
        // требуется установить темплейт виджета
        // widgetView.setPendingIntentTemplate(R.id.widgetList, clickPendingIntentTemplate)
        rv.setOnClickFillInIntent(R.id.itemAppWidget, Intent())// Здесь достаточно пустого интента

        // Получаем настройки из приложения
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
            0x151414
        )
        val holidayTextColor = prefs.getInt(
            applicationContext.getString(R.string.key_widget_holiday_font_color_preference),
            0x151414
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
            when (it.getString(4)) {
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
                    bindTimeWithEventTimeTextView(isShowingEventTime,rv,it)
                }

                EventType.BIRTHDAY.toString() -> {
                    setWidgetLineParameters(
                        rv,
                        birthdayTextColor,
                        sizeFontWidget
                    )
                    bindAgeWithEventTimeTextView(isShowingAge, rv, it)
                }
            }
            rv.setTextViewText(R.id.widgetEventTitleTextview, it.getString(1))
            if (isShowingEventDate) {
                with(it.getInt(2)){
                    val date = String.format("%02d", this%100) + "-" +
                            String.format("%02d", this/100%100) + "-" +
                            (this/10000).toString()
                    rv.setTextViewText(R.id.widgetEventDateTextview, date)
                    rv.setViewVisibility(R.id.widgetEventDateTextview, View.VISIBLE)
                }
            } else {
                rv.setViewVisibility(R.id.widgetEventDateTextview, View.GONE)
            }

            // rv.setTextViewText(R.id.widgetEventTimeTextview, it.getString(3))
        }
        return rv


    }

    private fun bindAgeWithEventTimeTextView(
        isShowingAge: Boolean,
        rv: RemoteViews,
        it: Cursor
    ) {
        if (isShowingAge) {
            rv.setTextViewText(R.id.widgetEventTimeTextview, it.getString(3))
            rv.setViewVisibility(R.id.widgetEventTimeTextview, View.VISIBLE)
        } else rv.setViewVisibility(R.id.widgetEventTimeTextview, View.GONE)
    }

    private fun bindTimeWithEventTimeTextView(
        isShowingEventTime: Boolean,
        rv: RemoteViews,
        it: Cursor
    ) {
        if (isShowingEventTime) {
            rv.setTextViewText(R.id.widgetEventTimeTextview, it.getString(3))
            rv.setViewVisibility(R.id.widgetEventTimeTextview, View.VISIBLE)
        } else rv.setViewVisibility(R.id.widgetEventTimeTextview, View.GONE)
    }

    private fun setWidgetLineParameters(rv: RemoteViews, color: Int, sizeFontWidget: Float) {
        with(rv) {
            setTextColor(R.id.widgetEventTitleTextview, color)
            setTextColor(R.id.widgetEventDateTextview, color)
            setTextColor(R.id.widgetEventTimeTextview, color)
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
        }
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        mCursor?.let {
            if (it.moveToPosition(position)) return it.getLong(0)
        }
        return position.toLong()
        //return if (mCursor?.moveToPosition(position) ?: false) mCursor.getLong(0) else position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}