package ru.geekbrains.eventsreminder.presentation.ui.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import ru.geekbrains.eventsreminder.R
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.android.support.DaggerDialogFragment
import ru.geekbrains.eventsreminder.databinding.CreateNewEventDialogFragmentBinding
import ru.geekbrains.eventsreminder.presentation.ui.SUCCESS_ID_TO_NAVIGATE


class CreateNewEventDialogFragment : DaggerDialogFragment() {
    private val binding: CreateNewEventDialogFragmentBinding by viewBinding()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corner_background)
            return inflater.inflate(R.layout.create_new_event_dialog_fragment, container, false)
        } catch (t: Throwable) {
            logAndToast(t)
            return null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.negativeBtnChooseNewEventType.setOnClickListener {
                try {
                    findNavController().navigate(R.id.homeToDashboard)
                } catch (t: Throwable) {
                    logAndToast(t)
                }
            }
            binding.positiveBtnChooseNewEventType.setOnClickListener {
                try {
                    val bundle = Bundle()
                    when (binding.radioGroupChooseNewEventType.checkedRadioButtonId) {
                        R.id.radiobtnBirthday -> {
                            bundle.putInt(
                                SUCCESS_ID_TO_NAVIGATE,
                                R.id.action_editBirthdayDialog_to_homeToDashboard
                            )
                            findNavController().navigate(
                                R.id.action_chooseNewEventTypeDialog_to_editBirthdayDialog,
                                bundle
                            )
                        }

                        R.id.radiobtnHoliday -> {
                            bundle.putInt(
                                SUCCESS_ID_TO_NAVIGATE,
                                R.id.action_editHolidayDialog_to_homeToDashboard
                            )
                            findNavController().navigate(
                                R.id.action_chooseNewEventTypeDialog_to_editHolidayDialog,
                                bundle
                            )
                        }

                        R.id.radiobtnAnotherType -> {
                            bundle.putInt(
                                SUCCESS_ID_TO_NAVIGATE,
                                R.id.action_editSimpleEventDialog_to_homeToDashboard
                            )
                            findNavController().navigate(
                                R.id.action_chooseNewEventTypeDialog_to_editSimpleEventDialog,
                                bundle
                            )
                        }

                        else -> {
                            Toast.makeText(
                                context,
                                getString(R.string.toast_msg_create_event_dialog),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (t: Throwable) {
                    logAndToast(t)
                }
            }
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }
    protected fun logAndToast(t:Throwable) = logAndToast(t,this::class.java.toString())

    protected fun logAndToast(t: Throwable, tag:String?) {
        try {
            Log.e(tag, "", t)
            Toast.makeText(requireContext().applicationContext, t.toString(), Toast.LENGTH_LONG).show()
        } catch (_: Throwable) {
        }
    }
}
