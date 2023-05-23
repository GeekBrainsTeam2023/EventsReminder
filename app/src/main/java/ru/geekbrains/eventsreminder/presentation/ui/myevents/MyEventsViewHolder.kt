package ru.geekbrains.eventsreminder.presentation.ui.myevents

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.databinding.MyEventsRecyclerviewItemBinding
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventSourceType
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.presentation.ui.SUCCESS_ID_TO_NAVIGATE
import ru.geekbrains.eventsreminder.presentation.ui.findActivity
import ru.geekbrains.eventsreminder.presentation.ui.toAgeInWordsByDate
import ru.geekbrains.eventsreminder.presentation.ui.toDaysSinceNowInWords
import java.time.format.DateTimeFormatter

class MyEventsViewHolder(view: View) : RecyclerView.ViewHolder(view), LifecycleOwner {
    private val binding: MyEventsRecyclerviewItemBinding by viewBinding()
    private val activity = view.context.findActivity()
    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    fun createLifecycle() {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    fun bind(
        item: EventData,
        isDataHeader: Boolean,
        viewModel: MyEventsViewModel,
    ) {
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
            myEventsEditBtn.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable(EventData::class.toString(),item)
                 when (item.type) {
                    EventType.BIRTHDAY -> {
                        bundle.putInt(SUCCESS_ID_TO_NAVIGATE,R.id.action_editBirthdayDialog_to_myEvents)
                        activity.findNavController(R.id.nav_host_fragment_activity_main)
                            .navigate(R.id.editBirthdayDialog, bundle)
                    }
                    EventType.HOLIDAY -> {
                        bundle.putInt(SUCCESS_ID_TO_NAVIGATE,R.id.action_editHolidayDialog_to_myEvents)
                        activity.findNavController(R.id.nav_host_fragment_activity_main)
                            .navigate(R.id.editHolidayDialog, bundle)
                    }
                    EventType.SIMPLE -> {
                        bundle.putInt(SUCCESS_ID_TO_NAVIGATE,R.id.action_editSimpleEventDialog_to_myEvents)
                        activity.findNavController(R.id.nav_host_fragment_activity_main)
                            .navigate(R.id.editSimpleEventDialog, bundle)
                    }
                }
            }
            myEventsDeleteBtn.setOnClickListener {
                confirmDeletionOfLocalEventDialog(viewModel, item)
            }
        }
    }

    private fun MyEventsRecyclerviewItemBinding.setCommonEventVisualisation(
        item: EventData,
        isDataHeader: Boolean
    ) {
        myEventsRecyclerViewItemTitleTextview.text = item.name
        textViewIntervalOfMyEvents.visibility =
            if (isDataHeader) {
                View.VISIBLE.also {
                    textViewIntervalOfMyEvents.text =
                        item.date.toDaysSinceNowInWords()
                }
            } else View.GONE
        textViewMyEventsDateOfEvents.visibility =
            if (isDataHeader) {
                View.VISIBLE.also {
                    textViewMyEventsDateOfEvents.text = item.date.format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    )
                }
            } else View.GONE
    }

    private fun MyEventsRecyclerviewItemBinding.setHolidayEventSpecifics(item: EventData) {
        myEventsRecyclerViewCardview.setCardBackgroundColor(
            activity.resources.getColor(
                R.color.light_violet,
                activity.theme
            )
        )
        myEventsRecyclerViewItemImage.setImageResource(R.drawable.local_holiday_icon)
        myEventsRecyclerViewItemAgeTextview.visibility = View.GONE
        if (item.sourceType != EventSourceType.LOCAL || item.time == null)
            myEventsRecyclerViewItemEventTimeTextview.visibility = View.GONE
        else {
            myEventsRecyclerViewItemEventTimeTextview.visibility = View.VISIBLE
            myEventsRecyclerViewItemEventTimeTextview.text =
                item.time.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }

    private fun MyEventsRecyclerviewItemBinding.setSimpleEventSpecifics(
        item: EventData
    ) {
        myEventsRecyclerViewCardview.setCardBackgroundColor(
            activity.resources.getColor(
                R.color.light_blue,
                activity.theme
            )
        )
        myEventsRecyclerViewItemAgeTextview.visibility = View.GONE
        myEventsRecyclerViewItemImage.setImageResource(R.drawable.local_simple_event_icon)
        if (item.time == null)
            myEventsRecyclerViewItemEventTimeTextview.visibility = View.GONE
        else {
            myEventsRecyclerViewItemEventTimeTextview.visibility = View.VISIBLE
            myEventsRecyclerViewItemEventTimeTextview.text =
                item.time.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }

    private fun MyEventsRecyclerviewItemBinding.setBirthdayEventSpecifics(
        item: EventData,
    ) {
        myEventsRecyclerViewCardview.setCardBackgroundColor(
            activity.resources.getColor(
                R.color.light_green,
                activity.theme
            )
        )
        myEventsRecyclerViewItemImage.setImageResource(R.drawable.local_birthday_icon)
        if (item.birthday != null && item.birthday.year != 0 && item.birthday <= item.date) {
            myEventsRecyclerViewItemAgeTextview.text =
                item.birthday.toAgeInWordsByDate(item.date)
            myEventsRecyclerViewItemAgeTextview.visibility = View.VISIBLE
        } else myEventsRecyclerViewItemAgeTextview.visibility = View.GONE
        myEventsRecyclerViewItemEventTimeTextview.visibility = View.GONE
    }

    private fun confirmDeletionOfLocalEventDialog(viewModel: MyEventsViewModel, item: EventData) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.delete_local_event_dialog_title))
            .setCancelable(true)
            .setPositiveButton(activity.getString(R.string.delete_local_events_dialog_positive_btn)) { _, id ->
                viewModel.deleteMyEvent(item)
                Toast.makeText(
                    activity.applicationContext,
                    activity.getString(R.string.toast_delete_local_event_dialod),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(activity.getString(R.string.delete_local_events_dialog_negative_btn)) { _, _ -> }
        val dlg = builder.create()
        dlg.show()
    }
}