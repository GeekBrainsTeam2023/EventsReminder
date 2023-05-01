package ru.geekbrains.eventsreminder.widget


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat.getColor
import androidx.preference.PreferenceManager
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.domain.EventType
import javax.inject.Inject


class WidgetRemoteViewsFactory
@Inject
constructor(
    private val applicationContext: Context,
) :
    RemoteViewsService.RemoteViewsFactory {
    //private val applicationContext: Context
    private var mCursor: Cursor? = null

//  init {
//        this.applicationContext = applicationContext
//    }

    override fun onCreate() {}
    override fun onDataSetChanged() {
        mCursor?.close()
        val identityToken = Binder.clearCallingIdentity()
        val uri: Uri = Contract.PATH_EVENTS_URI
        mCursor = applicationContext.contentResolver.query(
            uri,
            null,
            null,
            null,
            Contract._ID + " ASC"
        )
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

        if (position % 2 == 0) {
            rv.setInt(R.id.itemAppWidget, "setBackgroundColor", backColor)
        } else {
            rv.setInt(R.id.itemAppWidget, "setBackgroundColor", altBackColor)
        }

        mCursor?.let {
            when (it.getString(4)) {
                EventType.SIMPLE.toString() ->
                    setWidgetLineParameters(
                        rv,
                        applicationContext.getColor(R.color.purple_500),
                        sizeFontWidget
                    )

                EventType.HOLIDAY.toString() ->
                    setWidgetLineParameters(
                        rv,
                        applicationContext.getColor(R.color.color_secondary),
                        sizeFontWidget
                    )

                EventType.BIRTHDAY.toString() ->
                    setWidgetLineParameters(
                        rv,
                        applicationContext.getColor(R.color.light_blue_900),
                        sizeFontWidget
                    )

            }
            rv.setTextViewText(R.id.widgetEventTitleTextview, it.getString(1))
            rv.setTextViewText(R.id.widgetEventDateTextview, it.getString(2))
            rv.setTextViewText(R.id.widgetEventTimeTextview, it.getString(3))
        }
        return rv
    }

    private fun setWidgetLineParameters(rv: RemoteViews, color: Int, sizeFontWidget: Float) {
        with(rv) {
            setTextColor(R.id.widgetEventTitleTextview, color)
            setTextColor(R.id.widgetEventDateTextview, color)
            setTextColor(R.id.widgetEventTimeTextview, color)
            setTextViewTextSize(
                R.id.widgetEventTitleTextview, COMPLEX_UNIT_SP,
                sizeFontWidget.toFloat()
            )
            setTextViewTextSize(
                R.id.widgetEventDateTextview, COMPLEX_UNIT_SP,
                sizeFontWidget.toFloat()
            )
            setTextViewTextSize(
                R.id.widgetEventTimeTextview, COMPLEX_UNIT_SP,
                sizeFontWidget.toFloat()
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