package ru.geekbrains.eventsreminder.presentation.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import ru.geekbrains.eventsreminder.R
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.geekbrains.eventsreminder.databinding.EditBirthdayEventDialogFragmentBinding
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.usecases.addBirthDayEventFromLocalEdit

class EditBirthdayEventDialogFragment : AbsDaggerDialogFragment() {
	private val binding: EditBirthdayEventDialogFragmentBinding by viewBinding()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return try {
			dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corner_background)
			inflater.inflate(R.layout.edit_birthday_event_dialog_fragment, container, false)
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
				eventData?.let { eventData ->
					applyExistingEventData(eventData)
					sourceId = eventData.sourceId
				}
				negativeBtnCreateBirthdayEvent.setOnClickListener {
					try {
						findNavController().navigateUp()
					} catch (t: Throwable) {
						logAndToast(t)
					}
				}
				positiveBtnCreateBirthdayEvent.setOnClickListener {
					try {
						if (inputNameBirthdayEditText.text.trim().isEmpty()) {
							Toast.makeText(
								requireContext(),
								getString(R.string.toast_msg_create_birthday_dialog),
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

	private fun EditBirthdayEventDialogFragmentBinding.saveEvent(
		sourceId: Long
	) {
		try {
			dashboardViewModel.addLocalEvent(
				addBirthDayEventFromLocalEdit(
					inputNameBirthdayEditText.text.toString(),
					inputBirthdayDatePicker.dayOfMonth,
					inputBirthdayDatePicker.month + 1,
					inputBirthdayDatePicker.year,
					settings.minutesForStartNotification,
					sourceId
				)
			)
		} catch (t: Throwable) {
			logAndToast(t)
		}
	}

	private fun EditBirthdayEventDialogFragmentBinding.applyExistingEventData(
		eventData: EventData
	) {
		try {
			inputNameBirthdayEditText.setText(eventData.name)
			inputBirthdayDatePicker.init(
				eventData.birthday?.year ?: eventData.date.year,
				eventData.date.monthValue - 1, eventData.date.dayOfMonth,
				null
			)
		} catch (t: Throwable) {
			logAndToast(t)
		}
	}
}