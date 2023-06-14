package ru.geekbrains.eventsreminder.domain

enum class PeriodType(val nameRus: String) {
	YEAR("год"),
	MONTH("месяц"),
	WEEK("неделю"),
	DAY("день");
	companion object {
		fun fromString(s: String) =
			when (s) {
				YEAR.nameRus -> YEAR
				MONTH.nameRus -> MONTH
				WEEK.nameRus -> WEEK
				DAY.nameRus -> DAY
				else -> null
			}
	}
	override fun toString() : String {
		return nameRus
	}

	fun getDays(): Long =
		when (this) {
			YEAR -> 365
			MONTH -> 30
			WEEK -> 7
			DAY -> 1
		}
}
