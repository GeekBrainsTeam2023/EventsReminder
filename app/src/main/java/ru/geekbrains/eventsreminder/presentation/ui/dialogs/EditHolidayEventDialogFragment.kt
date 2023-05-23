package ru.geekbrains.eventsreminder.presentation.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerDialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.databinding.EditHolidayEventDialogFragmentBinding
import ru.geekbrains.eventsreminder.di.ViewModelFactory
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.presentation.ui.SUCCESS_ID_TO_NAVIGATE
import ru.geekbrains.eventsreminder.presentation.ui.dashboard.DashboardViewModel
import ru.geekbrains.eventsreminder.presentation.ui.parcelable
import ru.geekbrains.eventsreminder.usecases.addHolidayEventFromLocalEdit
import javax.inject.Inject

class EditHolidayEventDialogFragment: DaggerDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val dashboardViewModel by viewModels<DashboardViewModel>({ this }) { viewModelFactory }
    @Inject
    lateinit var settings: SettingsData
    private val binding: EditHolidayEventDialogFragmentBinding by viewBinding()
    private lateinit var eventData: EventData
    private var successIdToNavigate : Int? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corner_background)
        return inflater.inflate(R.layout.edit_holiday_event_dialog_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.parcelable<EventData>(EventData::class.toString())?.let {eventData = it}
        arguments?.getInt(SUCCESS_ID_TO_NAVIGATE)?.let { successIdToNavigate = it }
        var sourceId = 0L
        with(binding){
            if(::eventData.isInitialized){
                inputEventNameEditText.setText(eventData.name)
                inputHolidayDatePicker.init(eventData.date.year,
                    eventData.date.monthValue - 1,eventData.date.dayOfMonth,
                    null)
                eventData.time?.let {
                    chooseHolidayTimePicker.hour = it.hour
                    chooseHolidayTimePicker.minute = it.minute
                    isTimePickerEnabled.isChecked = true
                    chooseHolidayTimePicker.visibility = VISIBLE
                }
                sourceId = eventData.sourceId
            }
            chooseHolidayTimePicker.setIs24HourView(true)
            isTimePickerEnabled.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) chooseHolidayTimePicker.visibility = VISIBLE
                else chooseHolidayTimePicker.visibility = GONE
            }
            negativeBtnCreateHolidayEvent.setOnClickListener{
                findNavController().navigateUp()
            }
            positiveBtnCreateHolidayEvent.setOnClickListener {
                if (inputEventNameEditText.text.trim().isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_msg_create_holiday_simple_dialog), Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val hours = if (isTimePickerEnabled.isChecked) chooseHolidayTimePicker.hour
                                else null
                    val minutes = if (isTimePickerEnabled.isChecked) chooseHolidayTimePicker.minute
                                else null
                    dashboardViewModel.addLocalEvent(addHolidayEventFromLocalEdit(
                        inputEventNameEditText.text.toString(),
                        inputHolidayDatePicker.dayOfMonth,
                        inputHolidayDatePicker.month + 1,
                        inputHolidayDatePicker.year,
                        hours,
                        minutes,
                        settings.minutesForStartNotification,
                        sourceId
                    ))
                    if(successIdToNavigate != null && successIdToNavigate != 0){
                        findNavController().navigate(successIdToNavigate!!)
                    }
                   else findNavController().navigateUp()
                }
            }
        }
    }
}