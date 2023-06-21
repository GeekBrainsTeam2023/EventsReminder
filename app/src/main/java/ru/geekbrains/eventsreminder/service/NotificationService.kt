package ru.geekbrains.eventsreminder.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventNotificationData
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.presentation.ui.MAX_YEAR
import ru.geekbrains.eventsreminder.presentation.ui.toAgeInWordsByDate
import ru.geekbrains.eventsreminder.presentation.ui.toLocalTime
import ru.geekbrains.eventsreminder.usecases.EVENTS_DATA
import ru.geekbrains.eventsreminder.usecases.MINUTES_FOR_START_NOTIFICATION
import ru.geekbrains.eventsreminder.usecases.NotificationUtils
import ru.geekbrains.eventsreminder.usecases.TIME_TO_START_NOTIFICATION
import ru.geekbrains.eventsreminder.usecases.addEventsListToNotificationEventsList
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.abs

class NotificationService
    : Service() {
    private var eventList = mutableListOf<EventNotificationData>()
    private val delay = 10000L
    private val handler = Handler()
    private lateinit var runnable: Runnable
    private var minutesForStartNotification = 10
    private var timeToStartNotification = 101000
    private var idNotification = 1

    override fun onCreate() {
        super.onCreate()
        NotificationUtils.createNotificationChannel(this)
        runnable = Runnable {
            findEvents()
            handler.postDelayed(runnable, delay)
        }
        handler.postDelayed(runnable, delay)
    }

    private fun findEvents() {
        //val eventListCopy = .toList()
        for (event in eventList) {
            val eventTime = event.time ?: timeToStartNotification.toLocalTime()
                .plusMinutes(minutesForStartNotification.toLong())
            val eventDayTime =
                LocalDateTime.parse(event.date.toString() + "T" + eventTime.toString())
            val dur = Duration.between(
                LocalDateTime.now(),
                eventDayTime
            ).toMinutes()
            if (dur > 0) {
                if (dur <= minutesForStartNotification && event.idNotification == null) {
                    val notificationAgeOrTime =
                        if (event.type == EventType.BIRTHDAY) {
                            if (event.birthday?.year != MAX_YEAR)
                                event.birthday?.toAgeInWordsByDate(event.date)
                            else ""
                        } else
                            (event.time?.toString() ?: "")
                    NotificationUtils.sendNotification(
                        this,
                        idNotification,
                        event.type.getString() + "         " +
                                notificationAgeOrTime,
                        event.name,
                        event.time?.let { eventDayTime }
                    )
                    event.idNotification = idNotification
                    idNotification += 1
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getIntExtra(MINUTES_FOR_START_NOTIFICATION, 15)
            ?.apply { minutesForStartNotification = this }
        intent?.getIntExtra(TIME_TO_START_NOTIFICATION, 101000)
            ?.apply { timeToStartNotification = this }
        intent?.getParcelableArrayListExtra<EventData>(EVENTS_DATA)?.apply {
            addEventsListToNotificationEventsList(eventList, this.toMutableList())
        }
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}