package ru.geekbrains.eventsreminder.widget

import android.content.Intent
import android.widget.RemoteViewsService


class MyWidgetRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return WidgetRemoteViewsFactory(this.applicationContext, intent)
    }
}