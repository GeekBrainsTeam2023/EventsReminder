package ru.geekbrains.eventsreminder.presentation.ui.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.databinding.EditHolidayEventDialogFragmentBinding
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.usecases.addHolidayEventFromLocalEdit

class EditHolidayEventDialogFragment : AbsDaggerDialogFragment() {
    private val binding: EditHolidayEventDialogFragmentBinding by viewBinding()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try{
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corner_background)
        return inflater.inflate(R.layout.edit_holiday_event_dialog_fragment, container, false)
        } catch (t: Throwable) {
            logAndToast(t)
            return null
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
                chooseHolidayTimePicker.setIs24HourView(true)
                isTimePickerEnabled.setOnCheckedChangeListener { _, isChecked ->
                    try {
                        if (isChecked) chooseHolidayTimePicker.visibility = VISIBLE
                        else chooseHolidayTimePicker.visibility = GONE
                    } catch (t: Throwable) {
                        logAndToast(t)
                    }
                }
                negativeBtnCreateHolidayEvent.setOnClickListener {
                    try {
                        findNavController().navigateUp()
                    } catch (t: Throwable) {
                        logAndToast(t)
                    }
                }
                positiveBtnCreateHolidayEvent.setOnClickListener {
                    try {
                        if (inputEventNameEditText.text.trim().isEmpty()) {
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

    private fun EditHolidayEventDialogFragmentBinding.saveEvent(
        sourceId: Long
    ) {
        try {
            val hours = if (isTimePickerEnabled.isChecked) chooseHolidayTimePicker.hour
            else null
            val minutes = if (isTimePickerEnabled.isChecked) chooseHolidayTimePicker.minute
            else null
            dashboardViewModel.addLocalEvent(
                addHolidayEventFromLocalEdit(
                    inputEventNameEditText.text.toString(),
                    inputHolidayDatePicker.dayOfMonth,
                    inputHolidayDatePicker.month + 1,
                    inputHolidayDatePicker.year,
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

    private fun EditHolidayEventDialogFragmentBinding.applyExistingEventData(
        eventData: EventData
    ) {
        try {
            inputEventNameEditText.setText(eventData.name)
            inputHolidayDatePicker.init(
                eventData.date.year,
                eventData.date.monthValue - 1, eventData.date.dayOfMonth,
                null
            )
            eventData.time?.let {
                chooseHolidayTimePicker.hour = it.hour
                chooseHolidayTimePicker.minute = it.minute
                isTimePickerEnabled.isChecked = true
                chooseHolidayTimePicker.visibility = VISIBLE
            }
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }
}