package ru.geekbrains.eventsreminder.presentation.ui.settings

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.domain.SettingsData


class WidgetPreviewPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {
    lateinit var settings: SettingsData

    fun applySettings(settingsData: SettingsData) {
        settings = settingsData
    }

    fun renew() {
        super.notifyChanged()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        try {
            with(holder) {
                setWidgetLinesBackgroundColor()
                setFirstPreviewItemAppearance()
                setSecondPreviewItemAppearance()
                setThirdPreviewItemAppearance()
                setForthPreviewItemAppearance()
                setFifthPreviewItemAppearance()
            }
        } catch (t: Throwable) {
            try {
                Log.e(this::class.java.toString(), "", t)
                Toast.makeText(
                    context,
                    t.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }catch(_:Throwable){}
        }
    }

    private fun PreferenceViewHolder.setFirstPreviewItemAppearance() {
        val tvFirstDate = findViewById(R.id.firstPreviewItemDate) as TextView
        tvFirstDate.textSize = settings.sizeFontWidget.toFloat()
        tvFirstDate.setTextColor(settings.colorHolidayFontWidget)
        val tvFirstTitle = findViewById(R.id.firstPreviewItemTitle) as TextView
        tvFirstTitle.textSize = settings.sizeFontWidget.toFloat()
        tvFirstTitle.setTextColor(settings.colorHolidayFontWidget)
        if (!settings.showDateEvent) {
            tvFirstDate.visibility = View.GONE
        } else tvFirstDate.visibility = View.VISIBLE

    }

    private fun PreferenceViewHolder.setSecondPreviewItemAppearance() {
        val tvSecondDate = findViewById(R.id.secondPreviewItemDate) as TextView
        tvSecondDate.textSize = settings.sizeFontWidget.toFloat()
        tvSecondDate.setTextColor(settings.colorBirthdayFontWidget)
        val tvSecondTitle = findViewById(R.id.secondPreviewItemTitle) as TextView
        tvSecondTitle.textSize = settings.sizeFontWidget.toFloat()
        tvSecondTitle.setTextColor(settings.colorBirthdayFontWidget)
        val tvSecondAge = findViewById(R.id.secondPreviewItemAge) as TextView
        tvSecondAge.textSize = settings.sizeFontWidget.toFloat()
        tvSecondAge.setTextColor(settings.colorBirthdayFontWidget)
        if (!settings.showDateEvent) {
            tvSecondDate.visibility = View.GONE
        } else tvSecondDate.visibility = View.VISIBLE

        if (!settings.showAge) {
            tvSecondAge.visibility = View.GONE
        } else tvSecondAge.visibility = View.VISIBLE
    }

    private fun PreferenceViewHolder.setThirdPreviewItemAppearance() {
        val tvThirdDate = findViewById(R.id.thirdPreviewItemDate) as TextView
        tvThirdDate.textSize = settings.sizeFontWidget.toFloat()
        tvThirdDate.setTextColor(settings.colorBirthdayFontWidget)
        val tvThirdTitle = findViewById(R.id.thirdPreviewItemTitle) as TextView
        tvThirdTitle.textSize = settings.sizeFontWidget.toFloat()
        tvThirdTitle.setTextColor(settings.colorBirthdayFontWidget)
        val tvThirdAge = findViewById(R.id.thirdPreviewItemAge) as TextView
        tvThirdAge.textSize = settings.sizeFontWidget.toFloat()
        tvThirdAge.setTextColor(settings.colorBirthdayFontWidget)
        if (!settings.showDateEvent) {
            tvThirdDate.visibility = View.GONE
        } else tvThirdDate.visibility = View.VISIBLE
        if (!settings.showAge) {
            tvThirdAge.visibility = View.GONE
        } else tvThirdAge.visibility = View.VISIBLE
    }

    private fun PreferenceViewHolder.setForthPreviewItemAppearance() {
        val tvForthDate = findViewById(R.id.forthPreviewItemDate) as TextView
        tvForthDate.textSize = settings.sizeFontWidget.toFloat()
        tvForthDate.setTextColor(settings.colorSimpleEventFontWidget)
        val tvForthTitle = findViewById(R.id.forthPreviewItemTitle) as TextView
        tvForthTitle.textSize = settings.sizeFontWidget.toFloat()
        tvForthTitle.setTextColor(settings.colorSimpleEventFontWidget)
        val tvForthTime = findViewById(R.id.forthPreviewItemTime) as TextView
        tvForthTime.textSize = settings.sizeFontWidget.toFloat()
        tvForthTime.setTextColor(settings.colorSimpleEventFontWidget)
        if (!settings.showDateEvent) {
            tvForthDate.visibility = View.GONE
        } else tvForthDate.visibility = View.VISIBLE
        if (!settings.showTimeEvent) {
            tvForthTime.visibility = View.GONE
        } else tvForthTime.visibility = View.VISIBLE
    }

    private fun PreferenceViewHolder.setFifthPreviewItemAppearance() {
        val tvFifthDate = findViewById(R.id.fifthPreviewItemDate) as TextView
        tvFifthDate.textSize = settings.sizeFontWidget.toFloat()
        tvFifthDate.setTextColor(settings.colorHolidayFontWidget)
        val tvFifthTitle = findViewById(R.id.fifthPreviewItemTitle) as TextView
        tvFifthTitle.textSize = settings.sizeFontWidget.toFloat()
        tvFifthTitle.setTextColor(settings.colorHolidayFontWidget)
        if (!settings.showDateEvent) {
            tvFifthDate.visibility = View.GONE
        } else tvFifthDate.visibility = View.VISIBLE
    }

    private fun PreferenceViewHolder.setWidgetLinesBackgroundColor(): Unit? {
        findViewById(R.id.firstItemPreviewLinearLayout)?.setBackgroundColor(settings.colorWidget)
        findViewById(R.id.thirdItemPreviewLinearLayout)?.setBackgroundColor(settings.colorWidget)
        findViewById(R.id.fifthItemPreviewLinearLayout)?.setBackgroundColor(settings.colorWidget)
        findViewById(R.id.secondItemPreviewLinearLayout)?.setBackgroundColor(settings.alternatingColorWidget)
        return findViewById(R.id.forthItemPreviewLinearLayout)?.setBackgroundColor(settings.alternatingColorWidget)
    }
}