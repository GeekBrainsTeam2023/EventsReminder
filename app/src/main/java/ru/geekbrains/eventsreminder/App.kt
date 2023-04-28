package ru.geekbrains.eventsreminder

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ru.geekbrains.eventsreminder.di.EventsReminderComponent

class App : DaggerApplication() {
    companion object {
        lateinit var eventsReminderComponent: EventsReminderComponent
    }

    override fun onCreate() {
        super.onCreate()

    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        eventsReminderComponent =
            ru.geekbrains.eventsreminder.di.DaggerEventsReminderComponent
                .builder()
                .withContext(applicationContext)
                .build()

        return eventsReminderComponent
    }
}