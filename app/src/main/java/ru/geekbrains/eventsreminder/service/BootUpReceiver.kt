package ru.geekbrains.eventsreminder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootUpReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, NotificationService::class.java))
    }
}