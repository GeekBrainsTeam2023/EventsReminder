package ru.geekbrains.eventsreminder.domain

enum class PeriodType(val nameRus: String) {
	YEAR("Год"),
	MONTH("Месяц"),
	WEEK("Неделя"),
	DAY("день");

	fun getDays(): Long =
		when (this) {
			YEAR -> 365
			MONTH -> 30
			WEEK -> 7
			DAY -> 1
		}
}
