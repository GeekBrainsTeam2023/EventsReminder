package ru.geekbrains.eventsreminder.presentation.ui.myevents

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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

class MyEventsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
	private val binding: MyEventsRecyclerviewItemBinding by viewBinding()
	private val activity = view.context.findActivity()
	lateinit var mViewModel: MyEventsViewModel
	lateinit var mItem : EventData
	fun bind(
		item: EventData,
		isDataHeader: Boolean,
		isSelected : Boolean,
		viewModel: MyEventsViewModel,
	) {
		try {
			mItem = item
			mViewModel = viewModel
			with(binding) {
				setEventSpecificMarkup(item)
				setCommonEventVisualisation(item, isDataHeader)
					itemView.setOnClickListener {
					try {
						openEditDialog(item)
					} catch (t: Throwable) {
						outputError(t)
					}
				}
				myEventsRecyclerViewCardview.isSelected=isSelected
			}
		} catch (t: Throwable) {
			outputError(t)
		}
	}

	private fun openEditDialog(item: EventData) {
		try {
			val bundle = Bundle()
			bundle.putParcelable(EventData::class.toString(), item)
			when (item.type) {
				EventType.BIRTHDAY -> {
					bundle.putInt(
						SUCCESS_ID_TO_NAVIGATE,
						R.id.action_editBirthdayDialog_to_myEvents
					)
					activity.findNavController(R.id.nav_host_fragment_activity_main)
						.navigate(R.id.editBirthdayDialog, bundle)
				}

				EventType.HOLIDAY -> {
					bundle.putInt(SUCCESS_ID_TO_NAVIGATE, R.id.action_editHolidayDialog_to_myEvents)
					activity.findNavController(R.id.nav_host_fragment_activity_main)
						.navigate(R.id.editHolidayDialog, bundle)
				}

				EventType.SIMPLE -> {
					bundle.putInt(
						SUCCESS_ID_TO_NAVIGATE,
						R.id.action_editSimpleEventDialog_to_myEvents
					)
					activity.findNavController(R.id.nav_host_fragment_activity_main)
						.navigate(R.id.editSimpleEventDialog, bundle)
				}
			}
		} catch (t: Throwable) {
			outputError(t)
		}
	}

	private fun MyEventsRecyclerviewItemBinding.setEventSpecificMarkup(item: EventData) {
		try {
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
		} catch (t: Throwable) {
			outputError(t)
		}
	}

	private fun MyEventsRecyclerviewItemBinding.setCommonEventVisualisation(
		item: EventData,
		isDataHeader: Boolean
	) {
		try {
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
		} catch (t: Throwable) {
			outputError(t)
		}
	}

	private fun MyEventsRecyclerviewItemBinding.setHolidayEventSpecifics(item: EventData) {
		try {
			myEventsRecyclerViewCardview.setCardBackgroundColor(
				activity.resources.getColor(
					R.color.light_violet,
					activity.theme
				)
			)
			myEventsRecyclerViewItemImage.setImageResource(R.drawable.local_holiday_icon)
			myEventsRecyclerViewItemAgeTextview.visibility = View.INVISIBLE
			if (item.sourceType != EventSourceType.LOCAL || item.time == null)
				myEventsRecyclerViewItemEventTimeTextview.visibility = View.INVISIBLE
			else {
				myEventsRecyclerViewItemEventTimeTextview.visibility = View.VISIBLE
				myEventsRecyclerViewItemEventTimeTextview.text =
					item.time.format(DateTimeFormatter.ofPattern("HH:mm"))
			}
		} catch (t: Throwable) {
			outputError(t)
		}
	}

	private fun MyEventsRecyclerviewItemBinding.setSimpleEventSpecifics(
		item: EventData
	) {
		try {
			myEventsRecyclerViewCardview.setCardBackgroundColor(
				activity.resources.getColor(
					R.color.light_blue,
					activity.theme
				)
			)
			myEventsRecyclerViewItemAgeTextview.visibility = View.INVISIBLE
			myEventsRecyclerViewItemImage.setImageResource(R.drawable.local_simple_event_icon)
			if (item.time == null)
				myEventsRecyclerViewItemEventTimeTextview.visibility = View.INVISIBLE
			else {
				myEventsRecyclerViewItemEventTimeTextview.visibility = View.VISIBLE
				myEventsRecyclerViewItemEventTimeTextview.text =
					item.time.format(DateTimeFormatter.ofPattern("HH:mm"))
			}
		} catch (t: Throwable) {
			outputError(t)
		}
	}

	private fun MyEventsRecyclerviewItemBinding.setBirthdayEventSpecifics(
		item: EventData,
	) {
		try {
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
			} else myEventsRecyclerViewItemAgeTextview.visibility = View.INVISIBLE
			myEventsRecyclerViewItemEventTimeTextview.visibility = View.INVISIBLE
		} catch (t: Throwable) {
			outputError(t)
		}
	}

	private fun outputError(t: Throwable) {
		try {
			Toast.makeText(activity.applicationContext, t.toString(), Toast.LENGTH_LONG).show()
			Log.e(this::class.java.toString(), "", t)
		} catch (_: Throwable) {
		}
	}
}