package ru.geekbrains.eventsreminder.presentation.ui.dialogs.BirthdayPickerHelper


import android.icu.util.Calendar
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale

class BirthdayPickerHelper{
    private var mTempDate: Calendar? = null

    private var mMinDate: Calendar? = null

    private var mMaxDate: Calendar? = null

    private val mDateFormat: DateFormat =
        SimpleDateFormat("dd/MM/yyyy")

    var mNumberOfMonths = 0

    var mShortMonths: Array<String> = arrayOf()

    constructor(){
        val locale = Locale.getDefault()
        mTempDate = getCalendarForLocale(mTempDate, locale)
        mMinDate = getCalendarForLocale(mMinDate, locale)
        mMaxDate = getCalendarForLocale(mMaxDate, locale)

        mNumberOfMonths = mTempDate!!.getActualMaximum(Calendar.MONTH) + 1
        mShortMonths = DateFormatSymbols().shortMonths

        if (usingNumericMonths()) {
             for (i in 0..mNumberOfMonths) {
                mShortMonths[i] = String.format("%d", i + 1)
            }
        }

    }

    private fun usingNumericMonths(): Boolean {
        return Character.isDigit(mShortMonths.get(Calendar.JANUARY).get(0))
    }
    private fun getCalendarForLocale(oldCalendar: Calendar?, locale: Locale): Calendar? {
        return if (oldCalendar == null) {
            Calendar.getInstance(locale)
        } else {
            val currentTimeMillis = oldCalendar.timeInMillis
            val newCalendar = Calendar.getInstance(locale)
            newCalendar.timeInMillis = currentTimeMillis
            newCalendar
        }
    }
}


