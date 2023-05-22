package ru.geekbrains.eventsreminder.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.viewModels
import dagger.android.AndroidInjector
import dagger.android.DaggerService
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector

import ru.geekbrains.eventsreminder.di.ViewModelFactory
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.presentation.ui.dashboard.DashboardViewModel
import ru.geekbrains.eventsreminder.repo.Repo
import ru.geekbrains.eventsreminder.repo.cache.CacheRepo
import ru.geekbrains.eventsreminder.usecases.EVENTS_DATA
import ru.geekbrains.eventsreminder.usecases.MINUTES_FOR_START_NOTIFICATION
import ru.geekbrains.eventsreminder.usecases.NotificationUtils
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class NotificationService
    : Service() {
    private val LOG_TAG = "myLogs"
    private val eventList = mutableListOf<EventData>()
    private val delay = 60000L
    private val handler = Handler()
    private lateinit var runnable: Runnable
    private var minutesForStartNotification = 10

    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "старт сервис - ")
        NotificationUtils.createNotificationChannel(this)
        runnable = Runnable {
            findEvents()
            handler.postDelayed(runnable, delay)
        }
        handler.postDelayed(runnable, delay)
    }

    private fun findEvents() {
        val eventListCopy = eventList.toList()
        for (event in eventListCopy) {
            val eventTime = event.time ?: LocalTime.parse("00:00:00.000")

            if (Duration.between(
                    LocalDateTime.now(),
                    LocalDateTime.parse(event.date.toString() + "T" + eventTime.toString())
                ).toMinutes() <= minutesForStartNotification
            ) {
                NotificationUtils.sendNotification(this, event.type.getString(), event.name)
                eventList.remove(event)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getIntExtra(MINUTES_FOR_START_NOTIFICATION, 15)
            ?.apply { minutesForStartNotification = this }
        intent?.getParcelableArrayListExtra<EventData>(EVENTS_DATA)?.apply {
            eventList.clear()
            eventList.addAll(this.toList())
            Log.d(LOG_TAG, "add list event" + eventList.size.toString())
        }
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}