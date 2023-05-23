package ru.geekbrains.eventsreminder.presentation.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.geekbrains.eventsreminder.R
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.android.support.DaggerDialogFragment
import ru.geekbrains.eventsreminder.databinding.EditBirthdayEventDialogFragmentBinding
import ru.geekbrains.eventsreminder.di.ViewModelFactory
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.presentation.ui.SUCCESS_ID_TO_NAVIGATE
import ru.geekbrains.eventsreminder.presentation.ui.dashboard.DashboardViewModel
import ru.geekbrains.eventsreminder.presentation.ui.parcelable
import ru.geekbrains.eventsreminder.usecases.addBirthDayEventFromLocalEdit
import javax.inject.Inject

class EditBirthdayEventDialogFragment : DaggerDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val dashboardViewModel by viewModels<DashboardViewModel>({ this }) { viewModelFactory }
    @Inject
    lateinit var settings: SettingsData
    private val binding: EditBirthdayEventDialogFragmentBinding by viewBinding()
    private lateinit var eventData: EventData
    private var successIdToNavigate: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corner_background)
        return inflater.inflate(R.layout.edit_birthday_event_dialog_fragment, container, false)
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
            if(::eventData.isInitialized){
                inputNameBirthdayEditText.setText(eventData.name)
                inputBirthdayDatePicker.init(
                    eventData.birthday?.year ?: eventData.date.year,
                eventData.date.monthValue - 1,eventData.date.dayOfMonth,
                    null)
                sourceId = eventData.sourceId
            }
            negativeBtnCreateBirthdayEvent.setOnClickListener {
                findNavController().navigateUp()
            }
            positiveBtnCreateBirthdayEvent.setOnClickListener {
                if (inputNameBirthdayEditText.text.trim().isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_msg_create_birthday_dialog), Toast.LENGTH_SHORT
                    ).show()
                } else {
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
                    if (successIdToNavigate != null && successIdToNavigate != 0)
                        findNavController().navigate(successIdToNavigate!!)
                    else
                        findNavController().navigateUp()
                }
            }
        }
    }
}