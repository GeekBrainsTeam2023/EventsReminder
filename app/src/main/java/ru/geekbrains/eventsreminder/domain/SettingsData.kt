package ru.geekbrains.eventsreminder.domain

import android.graphics.Color

data class SettingsData(
    var isDataContact: Boolean = true,
    var isDataCalendar: Boolean = true,
    var daysForShowEvents: Int = 365,
    var minutesForStartNotification: Int = 15,
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
    var colorBirthdayFontWidget: Int = 0x01579B,
    var colorHolidayFontWidget: Int = 0x3700B3,
    var colorSimpleEventFontWidget: Int = 0x151414,
    )
