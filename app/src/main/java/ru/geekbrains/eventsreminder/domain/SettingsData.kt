package ru.geekbrains.eventsreminder.domain

import android.graphics.Color

data class SettingsData(
    var isDataContact: Boolean = true,
    var isDataCalendar: Boolean = true,
    var daysForShowEvents: Int = 365,
    val minutesForStartNotification: Int = 15,
    var showDateEvent: Boolean = true,
    var showTimeEvent: Boolean = true,
    var showAge: Boolean = true,
    val showBirthday: Boolean = true,
    val colorSimpleEvent: Int = Color.BLUE,
    val colorBirthdayEvent: Int = Color.GREEN,
    val colorHolidayEvent: Int = Color.RED,
    var daysForShowEventsWidget: Int = 365,
    var colorWidget: Int = 0xE8E6EC,
    var alternatingColorWidget: Int = 0xDCF3F3,
    var sizeFontWidget: Int = 13,
    var colorBirthdayFontWidget: Int = 0x151414,
    var colorHolidayFontWidget: Int = 0x151414,
    var colorSimpleEventFontWidget: Int = 0x151414,
    )
