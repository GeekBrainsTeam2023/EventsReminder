package ru.geekbrains.eventsreminder.domain

import android.graphics.Color

data class SettingsData(
    val isDataContact: Boolean = true,
    val isDataCalendar: Boolean = true,
    val minutesForStartNotification: Int = 15,
    val showDateEvent: Boolean = true,
    val showTimeEvent: Boolean = true,
    val showAge: Boolean = true,
    val showBirthday: Boolean = true,
    val colorSimpleEvent: Int = Color.BLUE,
    val colorBirthdayEvent: Int = Color.GREEN,
    val colorHolidayEvent: Int = Color.RED,
    val sizeWidget: SizeWidgetType = SizeWidgetType.Small,
    val colorWidget: Int = Color.BLUE,
    val transparentWidget: Int = 50,
    val sizeFontWidget: Int = 12,
    )
