package ru.geekbrains.eventsreminder.widget

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.android.AndroidInjection
import ru.geekbrains.eventsreminder.App
import javax.inject.Inject


class MyWidgetRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return WidgetRemoteViewsFactory(applicationContext)
    }
}