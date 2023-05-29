package ru.geekbrains.eventsreminder.presentation.ui.dialogs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.databinding.EditSimpleEventDialogFragmentBinding
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.usecases.addSimpleEventFromLocalEdit

class EditSimpleEventDialogFragment : AbsDaggerDialogFragment() {
	private val binding: EditSimpleEventDialogFragmentBinding by viewBinding()
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return try {
			dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corner_background)
			inflater.inflate(R.layout.edit_simple_event_dialog_fragment, container, false)
		} catch (t: Throwable) {
			logAndToast(t)
			null
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		try {
			processBundleArguments()
			var sourceId = 0L
			with(binding) {
				eventData?.let {
					applyExistingEventData(it)
					sourceId = it.sourceId
				}
				chooseSimpleEventTimePicker.setIs24HourView(true)
				simpleDialogIsTimePickerEnabled.setOnCheckedChangeListener { _, isChecked ->
					try {
						if (isChecked) chooseSimpleEventTimePicker.visibility = View.VISIBLE
						else chooseSimpleEventTimePicker.visibility = View.GONE
					} catch (t: Throwable) {
						logAndToast(t)
					}
				}
				negativeBtnCreateSimpleEvent.setOnClickListener {
					try {
						findNavController().navigateUp()
					} catch (t: Throwable) {
						logAndToast(t)
					}
				}
				positiveBtnCreateSimpleEvent.setOnClickListener {
					try {
						if (inputSimpleEventNameEditText.text.trim().isEmpty()) {
							Toast.makeText(
								requireContext(),
								getString(R.string.toast_msg_create_holiday_simple_dialog),
								Toast.LENGTH_SHORT
							).show()
						} else {
							saveEvent(sourceId)
							navigateOnSuccess()
						}
					} catch (t: Throwable) {
						logAndToast(t)
					}
				}
			}
		} catch (t: Throwable) {
			logAndToast(t)
		}
	}

	private fun EditSimpleEventDialogFragmentBinding.saveEvent(
		sourceId: Long
	) {
		try {
			val hours =
				if (simpleDialogIsTimePickerEnabled.isChecked) chooseSimpleEventTimePicker.hour
				else null
			val minutes =
				if (simpleDialogIsTimePickerEnabled.isChecked) chooseSimpleEventTimePicker.minute
				else null
			dashboardViewModel.addLocalEvent(
				addSimpleEventFromLocalEdit(
					inputSimpleEventNameEditText.text.toString(),
					inputSimpleEventDatePicker.dayOfMonth,
					inputSimpleEventDatePicker.month + 1,
					inputSimpleEventDatePicker.year,
					hours,
					minutes,
					settings.minutesForStartNotification,
					sourceId
				)
			)
		} catch (t: Throwable) {
			logAndToast(t)
		}
	}

	private fun EditSimpleEventDialogFragmentBinding.applyExistingEventData(eventData: EventData) {
		try {
			inputSimpleEventNameEditText.setText(eventData.name)
			inputSimpleEventDatePicker.init(
				eventData.date.year, eventData.date.month.value - 1,
				eventData.date.dayOfMonth, null
			)
			eventData.time?.let {
				chooseSimpleEventTimePicker.hour = it.hour
				chooseSimpleEventTimePicker.minute = it.minute
				simpleDialogIsTimePickerEnabled.isChecked = true
				chooseSimpleEventTimePicker.visibility = View.VISIBLE
			}
		} catch (t: Throwable) {
			logAndToast(t)
		}
	}
}