package ru.geekbrains.eventsreminder.presentation.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.R.color.light_green
import ru.geekbrains.eventsreminder.databinding.DashboardRecyclerviewItemBinding
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventSourceType
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.presentation.MainActivity
import ru.geekbrains.eventsreminder.presentation.ui.EVENT_ID
import ru.geekbrains.eventsreminder.presentation.ui.findActivity
import ru.geekbrains.eventsreminder.presentation.ui.toAgeInWordsByDate
import ru.geekbrains.eventsreminder.presentation.ui.toDaysSinceNowInWords
import java.time.format.DateTimeFormatter


class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding: DashboardRecyclerviewItemBinding by viewBinding()
    private val activity = view.context.findActivity() as MainActivity

    /**
     * Привязать к вьюхолдеру конкретный EventData
     * @param item EvenData для привязки
     * @param isDataHeader Показывать ли в карточке Дату вверху карточки
     * */
    fun bind(item: EventData, isDataHeader: Boolean) {
        try {
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
                root.setOnClickListener {
                    setClickActions(item)
                }
            }
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }

    private fun setClickActions(item: EventData) {
        try {
            when (item.sourceType) {
                EventSourceType.LOCAL ->
                    onLocalItemClicked(item)

                EventSourceType.CONTACTS ->
                    onContactsItemClicked(item)

                EventSourceType.CALENDAR ->
                    onCalendarItemClicked(item)
            }
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }

    private fun onCalendarItemClicked(item: EventData) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(
                "content://com.android.calendar/events/" + java.lang.String.valueOf(
                    item.sourceId
                )
            )
            intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_NO_HISTORY
                    or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            activity.startActivity(intent)
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }

    private fun onContactsItemClicked(item: EventData) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val contactUri = Uri.withAppendedPath(
                ContactsContract.Contacts.CONTENT_URI,
                java.lang.String.valueOf(item.sourceId)
            )
            intent.data = contactUri
            activity.startActivity(intent)
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }

    private fun onLocalItemClicked(item: EventData) {
        try {
            val bundle = Bundle()
            bundle.putLong(
                EVENT_ID,
                item.sourceId
            )
            activity.findNavController(R.id.nav_host_fragment_activity_main)
                .navigate(R.id.myEvents, bundle)
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }

    private fun DashboardRecyclerviewItemBinding.setCommonEventVisualisation(
        item: EventData,
        isDataHeader: Boolean
    ) {
        try {
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
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }

    private fun DashboardRecyclerviewItemBinding.setHolidayEventSpecifics(item: EventData) {
        try {
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
            dashboardRecyclerViewItemAgeTextview.visibility = INVISIBLE
            if (item.sourceType != EventSourceType.LOCAL || item.time == null)
                dashboardRecyclerViewItemEventTimeTextview.visibility = INVISIBLE
            else {
                dashboardRecyclerViewItemEventTimeTextview.visibility = VISIBLE
                dashboardRecyclerViewItemEventTimeTextview.text =
                    item.time.format(DateTimeFormatter.ofPattern("HH:mm"))
            }
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }

    private fun DashboardRecyclerviewItemBinding.setSimpleEventSpecifics(
        item: EventData
    ) {
        try {
            dashboardRecyclerViewCardview.setCardBackgroundColor(
                activity.resources.getColor(
                    R.color.light_blue,
                    activity.theme
                )
            )
            dashboardRecyclerViewItemAgeTextview.visibility = INVISIBLE
            if (item.sourceType == EventSourceType.LOCAL)
                dashboardRecyclerViewItemImage.setImageResource(R.drawable.local_simple_event_icon)
            else
                dashboardRecyclerViewItemImage.setImageResource(R.drawable.simple_event_icon)
            if (item.time == null)
                dashboardRecyclerViewItemEventTimeTextview.visibility = INVISIBLE
            else {
                dashboardRecyclerViewItemEventTimeTextview.visibility = VISIBLE
                dashboardRecyclerViewItemEventTimeTextview.text =
                    item.time.format(DateTimeFormatter.ofPattern("HH:mm"))
            }
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }

    private fun DashboardRecyclerviewItemBinding.setBirthdayEventSpecifics(
        item: EventData,
    ) {
        try {
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
            } else dashboardRecyclerViewItemAgeTextview.visibility = INVISIBLE
            dashboardRecyclerViewItemEventTimeTextview.visibility = INVISIBLE
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }


    private fun logAndToast(t: Throwable) = logAndToast(t, this::class.java.toString())

    private fun logAndToast(t: Throwable, TAG: String) {
        try {
            Log.e(TAG, "", t)
            Toast.makeText(activity.applicationContext, t.toString(), Toast.LENGTH_LONG).show()
        } catch (_: Throwable) {
        }
    }
}