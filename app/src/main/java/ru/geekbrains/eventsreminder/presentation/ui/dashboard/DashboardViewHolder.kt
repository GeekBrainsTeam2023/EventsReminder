package ru.geekbrains.eventsreminder.presentation.ui.dashboard

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.R.color.light_green
import ru.geekbrains.eventsreminder.databinding.DashboardRecyclerviewItemBinding
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.presentation.ui.RusIntPlural
import ru.geekbrains.eventsreminder.presentation.ui.toAgeInWordsByDate
import ru.geekbrains.eventsreminder.presentation.ui.toDaysSinceNowInWords
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class DashboardViewHolder (view: View) : RecyclerView.ViewHolder(view), LifecycleOwner {
    private val binding: DashboardRecyclerviewItemBinding by viewBinding()
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val activity = view.context.findActivity()
    private var paused: Boolean = false

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    fun createLifecycle() {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    fun attachToWindow() {
        if (paused) {
            lifecycleRegistry.currentState = Lifecycle.State.RESUMED
            paused = false
        } else {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }
    }

    fun detachFromWindow() {
        if (!paused) {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
            paused = true
        }
    }

    /**
     * Привязать к вьюхолдеру конкретный EventData
     * @param item EvenData для привязки
     * @param isDataHeader Показывать ли в карточке Дату вверху карточки
     * */
    fun bind(item: EventData, isDataHeader: Boolean) {
        with(binding) {
            when (item.type) {
                EventType.BIRTHDAY -> {
                    setBirthdayEventSpecifics(item)
                }

                EventType.HOLIDAY -> {
                    setHolidayEventSpecifics()
                }

                EventType.SIMPLE -> {
                    setSimpleEventSpecifics(item)
                }
            }
            setCommonEventVisualisation(item, isDataHeader)
        }
    }

    private fun DashboardRecyclerviewItemBinding.setCommonEventVisualisation(
        item: EventData,
        isDataHeader: Boolean
    ) {
        dashboardRecyclerViewItemTitleTextview.text = item.name
        textViewDashboardIntervalOfEvents.visibility =
            if (isDataHeader) {
                VISIBLE.also {
                    textViewDashboardIntervalOfEvents.text =
                        item.date.toDaysSinceNowInWords()
                }
            } else GONE

        textViewDashboardDateOfEvents.visibility =
            if (isDataHeader) {
                VISIBLE.also {
                    textViewDashboardDateOfEvents.text = item.date.format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    )
                }
            } else GONE
    }

    private fun DashboardRecyclerviewItemBinding.setHolidayEventSpecifics() {
        dashboardRecyclerViewCardview.setCardBackgroundColor(
            activity.resources.getColor(
                R.color.light_violet,
                activity.theme
            )
        )
        dashboardRecyclerViewItemImage.setImageResource(R.drawable.holiday_icon_1)
        dashboardRecyclerViewItemAgeTextview.visibility = GONE
        dashboardRecyclerViewItemEventTimeTextview.visibility = GONE
    }

    private fun DashboardRecyclerviewItemBinding.setSimpleEventSpecifics(
        item: EventData,
    ) {
        dashboardRecyclerViewCardview.setCardBackgroundColor(
            activity.resources.getColor(
                R.color.light_blue,
                activity.theme
            )
        )
        dashboardRecyclerViewItemAgeTextview.visibility = GONE
        dashboardRecyclerViewItemEventTimeTextview.visibility = VISIBLE
        dashboardRecyclerViewItemImage.setImageResource(R.drawable.simple_event_icon)
        dashboardRecyclerViewItemEventTimeTextview.text =
            item.time.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    private fun DashboardRecyclerviewItemBinding.setBirthdayEventSpecifics(
        item: EventData,
    ) {
        dashboardRecyclerViewCardview.setCardBackgroundColor(
            activity.resources.getColor(
                light_green,
                activity.theme
            )
        )
        dashboardRecyclerViewItemImage.setImageResource(R.drawable.birthday_balloons)
        if (item.birthday != null && item.birthday.year != 0 && item.birthday <= item.date) {
            dashboardRecyclerViewItemAgeTextview.text =
                item.birthday.toAgeInWordsByDate(item.date)

                //dateToAgeInWords(item.birthday,item.date)
            dashboardRecyclerViewItemAgeTextview.visibility = VISIBLE
        }
        else dashboardRecyclerViewItemAgeTextview.visibility = GONE
        dashboardRecyclerViewItemEventTimeTextview.visibility = GONE
    }

    fun dateToDaysInWords(date: LocalDate) =
        when (ChronoUnit.DAYS.between(LocalDate.now(), date).toInt()) {
            0 -> "Сегодня"
            1 -> "Завтра"
            2 -> "Послезавтра"
            else -> "Через " + RusIntPlural(
                "д",
                ChronoUnit.DAYS.between(LocalDate.now(), date).toInt(),
                "ень", "ня", "ней"
            ).toString()
        }

    fun dateToAgeInWords(birthday: LocalDate, date: LocalDate) =
        "исполнится " + RusIntPlural(
            "",
            ChronoUnit.YEARS.between(birthday,date).toInt(),
            "год", "года", "лет"
        )


    tailrec fun Context.findActivity(): Activity {
        if (this is Activity) {
            return this
        } else {
            if (this is ContextWrapper) {
                return this.baseContext.findActivity()
            }
            throw java.lang.IllegalStateException("Context chain has no activity")
        }
    }
}