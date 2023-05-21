package ru.geekbrains.eventsreminder.domain

enum class EventType {
    SIMPLE,
    BIRTHDAY,
    HOLIDAY;
    fun getString(): String =
        when (this) {
            SIMPLE -> "Событие"
            BIRTHDAY -> "День Рождения"
            HOLIDAY -> "Праздник"
        }

}