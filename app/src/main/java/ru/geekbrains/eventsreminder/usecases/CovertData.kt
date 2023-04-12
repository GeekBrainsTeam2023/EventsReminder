package ru.geekbrains.eventsreminder.usecases

import android.os.Build
import androidx.annotation.RequiresApi
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.domain.PeriodType
import java.time.LocalDate
import java.time.LocalTime


    @RequiresApi(Build.VERSION_CODES.O)
    fun addBirthDayEventFromContactPhone(name:String, birthDay:String) : EventData {
        val date =LocalDate.of(LocalDate.now().year,
            LocalDate.parse(birthDay).monthValue,
            LocalDate.parse(birthDay).dayOfMonth)
        return EventData(
            EventType.BIRTHDAY,
            PeriodType.YEAR,
            LocalDate.parse(birthDay),
            date,
            LocalTime.parse("00:00:00"),
            LocalTime.parse("00:15:00"),
            name
        )
    }