package ru.geekbrains.eventsreminder.presentation.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import ru.geekbrains.eventsreminder.R
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.android.support.DaggerDialogFragment
import ru.geekbrains.eventsreminder.databinding.CreateNewEventDialogFragmentBinding


class CreateNewEventDialogFragment : DaggerDialogFragment() {
    private val binding: CreateNewEventDialogFragmentBinding by viewBinding()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corner_background)
        return inflater.inflate(R.layout.create_new_event_dialog_fragment, container, false)}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.negativeBtnChooseNewEventType.setOnClickListener{
            findNavController().navigate(R.id.homeToDashboard)
        }
        binding.positiveBtnChooseNewEventType.setOnClickListener{
            when(binding.radioGroupChooseNewEventType.checkedRadioButtonId){
                R.id.radiobtnBirthday -> findNavController().navigate(R.id.createBirthdayDialog)
                R.id.radiobtnHoliday -> findNavController().navigate(R.id.createHolidayDialog)
                R.id.radiobtnAnotherType -> findNavController().navigate(R.id.createSimpleEventDialog)
                else -> {
                    Toast.makeText(context,getString(R.string.toast_msg_create_event_dialog),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
