package ru.geekbrains.eventsreminder.presentation.ui.dashboard

/**
 * Класс для вывода числительных с правильными окончаниями в зависимости от количества
 * */
data class RusIntPlural(
    val name: String,
    val number: Int,
    val singleEnding: String = "",
    val twoToFourEnding: String = "",
    val fiveToTenEnding: String = ""
) {
    override fun toString(): String {
        val suffix =
        if ((number / 10) % 10 != 1) {
            val num = number % 10
            if (num == 1) singleEnding
            else if (num > 1 && num < 5)  twoToFourEnding
            else fiveToTenEnding
        } else fiveToTenEnding
        return "$number $name$suffix"
    }
}