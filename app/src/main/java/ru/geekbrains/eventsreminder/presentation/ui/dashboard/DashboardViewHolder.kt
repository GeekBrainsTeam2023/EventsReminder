package ru.geekbrains.eventsreminder.presentation.ui.dashboard

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
import ru.geekbrains.eventsreminder.domain.EventSourceType
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.presentation.ui.findActivity
import ru.geekbrains.eventsreminder.presentation.ui.toAgeInWordsByDate
import ru.geekbrains.eventsreminder.presentation.ui.toDaysSinceNowInWords
import java.time.format.DateTimeFormatter

class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view), LifecycleOwner {
    private val binding: DashboardRecyclerviewItemBinding by viewBinding()
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val activity = view.context.findActivity()
    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    fun createLifecycle() {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

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
                    setHolidayEventSpecifics(item)
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

    private fun DashboardRecyclerviewItemBinding.setHolidayEventSpecifics(item: EventData) {
        dashboardRecyclerViewCardview.setCardBackgroundColor(
            activity.resources.getColor(
                R.color.light_violet,
                activity.theme
            )
        )
        if (item.sourceType == EventSourceType.LOCAL)
            dashboardRecyclerViewItemImage.setImageResource(R.drawable.local_holiday_icon)
        else
            dashboardRecyclerViewItemImage.setImageResource(R.drawable.holiday_icon_1)
        dashboardRecyclerViewItemAgeTextview.visibility = GONE
        if (item.sourceType != EventSourceType.LOCAL || item.time == null)
            dashboardRecyclerViewItemEventTimeTextview.visibility = GONE
        else {
            dashboardRecyclerViewItemEventTimeTextview.visibility = VISIBLE
            dashboardRecyclerViewItemEventTimeTextview.text =
                item.time.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }

    private fun DashboardRecyclerviewItemBinding.setSimpleEventSpecifics(
        item: EventData
    ) {
        dashboardRecyclerViewCardview.setCardBackgroundColor(
            activity.resources.getColor(
                R.color.light_blue,
                activity.theme
            )
        )
        dashboardRecyclerViewItemAgeTextview.visibility = GONE
        if (item.sourceType == EventSourceType.LOCAL)
            dashboardRecyclerViewItemImage.setImageResource(R.drawable.local_simple_event_icon)
        else
            dashboardRecyclerViewItemImage.setImageResource(R.drawable.simple_event_icon)
        if (item.time == null)
            dashboardRecyclerViewItemEventTimeTextview.visibility = GONE
        else {
            dashboardRecyclerViewItemEventTimeTextview.visibility = VISIBLE
            dashboardRecyclerViewItemEventTimeTextview.text =
                item.time.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
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
        if (item.sourceType == EventSourceType.LOCAL)
            dashboardRecyclerViewItemImage.setImageResource(R.drawable.local_birthday_icon)
        else
            dashboardRecyclerViewItemImage.setImageResource(R.drawable.birthday_balloons)
        if (item.birthday != null && item.birthday.year != 0 && item.birthday <= item.date) {
            dashboardRecyclerViewItemAgeTextview.text =
                item.birthday.toAgeInWordsByDate(item.date)
            dashboardRecyclerViewItemAgeTextview.visibility = VISIBLE
        } else dashboardRecyclerViewItemAgeTextview.visibility = GONE
        dashboardRecyclerViewItemEventTimeTextview.visibility = GONE
    }
}