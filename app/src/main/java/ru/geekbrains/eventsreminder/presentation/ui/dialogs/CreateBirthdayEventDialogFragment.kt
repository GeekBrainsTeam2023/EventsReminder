package ru.geekbrains.eventsreminder.presentation.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ru.geekbrains.eventsreminder.R
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.android.support.DaggerDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.geekbrains.eventsreminder.databinding.CreateBirthdayEventDialogFragmentBinding
import ru.geekbrains.eventsreminder.repo.local.LocalRepo
import ru.geekbrains.eventsreminder.usecases.addBirthDayEventFromLocalEdit
import javax.inject.Inject

class CreateBirthdayEventDialogFragment : DaggerDialogFragment() {
    @Inject
    lateinit var localRepo: LocalRepo
    private val binding: CreateBirthdayEventDialogFragmentBinding by viewBinding()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corner_background)
        return inflater.inflate(R.layout.create_birthday_event_dialog_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
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
                    lifecycleScope.launch(Dispatchers.IO) {
                        localRepo.addEvent(
                            addBirthDayEventFromLocalEdit(
                                inputNameBirthdayEditText.text.toString(),
                                inputBirthdayDatePicker.dayOfMonth,
                                inputBirthdayDatePicker.month + 1,
                                inputBirthdayDatePicker.year
                            )
                        )
                    }
                    findNavController().navigate(R.id.homeToDashboard)
                }
            }
        }
    }
}