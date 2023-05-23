package ru.geekbrains.eventsreminder.presentation.ui.dialogs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.android.support.DaggerDialogFragment
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.databinding.EditSimpleEventDialogFragmentBinding
import ru.geekbrains.eventsreminder.di.ViewModelFactory
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.presentation.ui.SUCCESS_ID_TO_NAVIGATE
import ru.geekbrains.eventsreminder.presentation.ui.dashboard.DashboardViewModel
import ru.geekbrains.eventsreminder.presentation.ui.parcelable
import ru.geekbrains.eventsreminder.usecases.addSimpleEventFromLocalEdit
import javax.inject.Inject

class EditSimpleEventDialogFragment : DaggerDialogFragment() {
    @Inject
    lateinit var settings: SettingsData
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val dashboardViewModel by viewModels<DashboardViewModel>({ this }) { viewModelFactory }
    private val binding: EditSimpleEventDialogFragmentBinding by viewBinding()
    private lateinit var eventData: EventData
    private var successIdToNavigate: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corner_background)
        return inflater.inflate(R.layout.edit_simple_event_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.parcelable<EventData>(EventData::class.toString())?.let {
            eventData = it
        }
        arguments?.getInt(SUCCESS_ID_TO_NAVIGATE)?.let {
            successIdToNavigate = it
        }
        var sourceId = 0L
        with(binding) {
            if (::eventData.isInitialized) {
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
                sourceId = eventData.sourceId
            }
            chooseSimpleEventTimePicker.setIs24HourView(true)
            simpleDialogIsTimePickerEnabled.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) chooseSimpleEventTimePicker.visibility = View.VISIBLE
                else chooseSimpleEventTimePicker.visibility = View.GONE
            }
            negativeBtnCreateSimpleEvent.setOnClickListener {
                findNavController().navigateUp()
            }
            positiveBtnCreateSimpleEvent.setOnClickListener {
                if (inputSimpleEventNameEditText.text.trim().isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_msg_create_holiday_simple_dialog),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
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
                    if (successIdToNavigate != null && successIdToNavigate != 0)
                        findNavController().navigate(successIdToNavigate!!)
                    else
                        findNavController().navigateUp()
                }
            }
        }
    }
}