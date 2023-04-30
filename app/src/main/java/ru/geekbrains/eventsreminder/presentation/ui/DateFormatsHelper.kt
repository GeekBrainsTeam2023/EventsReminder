package ru.geekbrains.eventsreminder.presentation.ui

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Перевести в словесное описание дней с сегоднешнего дня ("Сегодня", "Завтра", "Через 3 дня")
 * */
    fun LocalDate.toDaysSinceNowInWords() =
        when (ChronoUnit.DAYS.between(LocalDate.now(), this).toInt()) {
            0 -> "Сегодня"
            1 -> "Завтра"
            2 -> "Послезавтра"
            else -> "Через " + RusIntPlural(
                "д",
                ChronoUnit.DAYS.between(LocalDate.now(), this).toInt(),
                "ень", "ня", "ней"
            ).toString()
        }
/**
 * Считая текущую дату днём рождения вывести в словесной форме возраст к заданой дате
 * ("1 год", "5 лет", "3 года")
 * @param [dateSinceBirthday] дата к которой выводить возраст
 * */
    fun LocalDate.toAgeInWordsByDate(dateSinceBirthday: LocalDate) =
         RusIntPlural(
            "",
            ChronoUnit.YEARS.between(this,dateSinceBirthday).toInt(),
            "год", "года", "лет"
        ).toString()
