package ru.geekbrains.eventsreminder.widget


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.presentation.MainActivity


class WidgetRemoteViewsFactory(applicationContext: Context, intent: Intent?) :
    RemoteViewsService.RemoteViewsFactory {
    private val mContext: Context
    private var mCursor: Cursor? = null

    init {
        mContext = applicationContext
    }

    override fun onCreate() {}
    override fun onDataSetChanged() {
        mCursor?.close()
        val identityToken = Binder.clearCallingIdentity()
        val uri: Uri = Contract.PATH_EVENTS_URI
        mCursor = mContext.contentResolver.query(
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

        val rv = RemoteViews(mContext.packageName, R.layout.item_app_widget)

        mCursor?.let {
            when (it.getString(4)) {
                EventType.SIMPLE.toString() ->
                    rv.setTextColor(
                        R.id.widgetEventTitleTextview,
                        mContext.getColor(R.color.color_primary_dark)
                    )
                EventType.HOLIDAY.toString() ->
                    rv.setTextColor(
                        R.id.widgetEventTitleTextview,
                        mContext.getColor(R.color.color_secondary)
                    )
                EventType.BIRTHDAY.toString() ->
                    rv.setTextColor(
                        R.id.widgetEventTitleTextview,
                        mContext.getColor(com.google.android.material.R.color.material_blue_grey_900)
                    )
            }
            rv.setTextViewText(R.id.widgetEventTitleTextview, it.getString(1))
            rv.setTextViewText(R.id.widgetEventDateTextview, it.getString(2))
            rv.setTextViewText(R.id.widgetEventTimeTextview, it.getString(3))
        }
        return rv
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