package ru.geekbrains.eventsreminder.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventNotificationData
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.presentation.ui.MAX_YEAR
import ru.geekbrains.eventsreminder.presentation.ui.parcelable
import ru.geekbrains.eventsreminder.presentation.ui.toAgeInWordsByDate
import ru.geekbrains.eventsreminder.presentation.ui.toLocalTime
import ru.geekbrains.eventsreminder.usecases.EVENTS_DATA
import ru.geekbrains.eventsreminder.usecases.MINUTES_FOR_START_NOTIFICATION
import ru.geekbrains.eventsreminder.usecases.NOTIFY_ABOUT_EVENT
import ru.geekbrains.eventsreminder.usecases.NotificationUtils
import ru.geekbrains.eventsreminder.usecases.TIME_TO_START_NOTIFICATION
import ru.geekbrains.eventsreminder.usecases.renewNotificationEventsList
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Collections.synchronizedList


class NotificationService
    : Service() {
    private var eventList = synchronizedList(mutableListOf<EventNotificationData>())
    private var minutesForStartNotification = 10
    private var timeToStartNotification = 101000
    private var idNotification = 1
    private var idAlarm = 1

    override fun onCreate() {
        super.onCreate()
        NotificationUtils.createNotificationChannel(this)
    }

    private fun findEvents() {
        synchronized(eventList) {
            val sortedList = eventList.sortedBy { event -> event.getNotifyTime() }
            for (event in sortedList) {
                var notifyTime = event.getNotifyTime()
                val dur = Duration.between(
                    LocalDateTime.now(),
                    notifyTime
                ).toMinutes()
                if (event.idAlarm != null || ((dur < 0) && (-dur > minutesForStartNotification)))
                    continue
                if (dur in -minutesForStartNotification .. minutesForStartNotification)
                    notifyTime = LocalDateTime.now().plusSeconds(10)

                val am: AlarmManager =
                    applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    idAlarm,
                    Intent(applicationContext, BootUpReceiver::class.java).apply {
                        putExtra(NOTIFY_ABOUT_EVENT, event)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                );

                am.setAlarmClock(
                    AlarmManager.AlarmClockInfo(
                        notifyTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        pendingIntent
                    ),
                    pendingIntent
                )
                event.idAlarm = idAlarm++
                break
            }

        }
    }

    fun EventNotificationData.getNotifyTime() = time?.let {
        LocalDateTime.of(
            date,
            it
        ).minusMinutes(
            minutesForStartNotification.toLong()
        )
    } ?: LocalDateTime.of(
        date, timeToStartNotification.toLocalTime()
    )

    private fun notifyAboutEvent(event: EventNotificationData) {
        val eventTime = event.time ?: (timeToStartNotification.toLocalTime()
            .plusMinutes(minutesForStartNotification.toLong()))
        val eventDayTime =
            LocalDateTime.of(event.date, eventTime)
        val dur = Duration.between(
            LocalDateTime.now(),
            eventDayTime
        ).toMinutes()
        if (event.idNotification == null &&
            dur in -minutesForStartNotification..minutesForStartNotification
        ) {
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getIntExtra(MINUTES_FOR_START_NOTIFICATION, 15)
            ?.apply { minutesForStartNotification = this }
        intent?.getIntExtra(TIME_TO_START_NOTIFICATION, 101000)
            ?.apply { timeToStartNotification = this }
        intent?.getParcelableArrayListExtra<EventData>(EVENTS_DATA)?.let {
            synchronized(eventList) {
                renewNotificationEventsList(eventList, it.toMutableList())
            }
        }
        intent?.parcelable<EventNotificationData>(NOTIFY_ABOUT_EVENT)?.let {
            Log.d("NOTIFY_ABOUT_EVENT", it.toString())
            synchronized(eventList) {
                eventList.find { event ->
                    event.name == it.name
                            && event.date == it.date
                            && event.time == it.time
                }?.let { found -> notifyAboutEvent(found) }
            }
        }
        findEvents()
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}