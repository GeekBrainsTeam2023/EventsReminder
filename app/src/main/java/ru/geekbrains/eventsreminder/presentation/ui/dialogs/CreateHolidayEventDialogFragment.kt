package ru.geekbrains.eventsreminder.presentation.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerDialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.databinding.CreateHolidayEventDialogFragmentBinding
import ru.geekbrains.eventsreminder.repo.local.LocalRepo
import ru.geekbrains.eventsreminder.usecases.addHolidayEventFromLocalEdit
import javax.inject.Inject

class CreateHolidayEventDialogFragment: DaggerDialogFragment() {
    @Inject
    lateinit var localRepo: LocalRepo
    private val binding: CreateHolidayEventDialogFragmentBinding by viewBinding()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corner_background)
        return inflater.inflate(R.layout.create_holiday_event_dialog_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
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
                    lifecycleScope.launch(Dispatchers.IO) {
                        localRepo.addEvent(
                            addHolidayEventFromLocalEdit(
                                inputEventNameEditText.text.toString(),
                                inputHolidayDatePicker.dayOfMonth,
                                inputHolidayDatePicker.month + 1,
                                inputHolidayDatePicker.year,
                                chooseHolidayTimePicker.hour,
                                chooseHolidayTimePicker.minute
                            )
                        )
                    }
                    findNavController().navigate(R.id.homeToDashboard)
                }
            }
        }
    }
}