package ru.geekbrains.eventsreminder.domain

import android.graphics.Color

data class SettingsData(
    var isDataContact: Boolean = true,
    var isDataCalendar: Boolean = true,
    var daysForShowEvents: Int = 365,
    val minutesForStartNotification: Int = 15,
    val showDateEvent: Boolean = true,
    val showTimeEvent: Boolean = true,
    val showAge: Boolean = true,
    val showBirthday: Boolean = true,
    val colorSimpleEvent: Int = Color.BLUE,
    val colorBirthdayEvent: Int = Color.GREEN,
    val colorHolidayEvent: Int = Color.RED,
    var colorWidget: Int = 0xE8E6EC,
    var alternatingColorWidget: Int = 0xDCF3F3,
    var sizeFontWidget: Int = 13,
    )
