package ru.geekbrains.eventsreminder

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ru.geekbrains.eventsreminder.di.EventsReminderComponent
import ru.geekbrains.eventsreminder.di.WidgetModule

class App : DaggerApplication() {
    companion object {
        lateinit var eventsReminderComponent: EventsReminderComponent
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        eventsReminderComponent =
            ru.geekbrains.eventsreminder.di.DaggerEventsReminderComponent
                .builder()
                .withApp(this)
                .withContext(applicationContext)
                .widgetModule(WidgetModule(applicationContext))
                .build()
        return eventsReminderComponent
    }

}