package ru.geekbrains.eventsreminder.presentation.ui.dialogs

import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerDialogFragment
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.di.ViewModelFactory
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.presentation.ui.SOURCE_ID_TO_NAVIGATE
import ru.geekbrains.eventsreminder.presentation.ui.dashboard.DashboardViewModel
import ru.geekbrains.eventsreminder.presentation.ui.parcelable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

abstract class AbsDaggerDialogFragment: DaggerDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    protected val dashboardViewModel by viewModels<DashboardViewModel>({ this }) { viewModelFactory }
    @Inject
    lateinit var settings: SettingsData
    private var sourceIdToNavigate : Int? = null
    var eventData: EventData? = null
    protected var hours: Int? = null
    protected var minutes: Int? = null
    protected abstract fun getSuccessIdToNavigate(sourceNavigationId:Int) :Int

    protected fun processBundleArguments() {
        try{
        arguments?.parcelable<EventData>(EventData::class.toString())?.let { eventData = it }
        arguments?.getInt(SOURCE_ID_TO_NAVIGATE)?.let { sourceIdToNavigate = it }
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }
    protected fun navigateOnSuccess() {
        try{
            findNavController().navigate(getSuccessIdToNavigate(sourceIdToNavigate ?: R.id.homeToDashboard))
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }

    protected fun setTimePickerListeners(textView: TextView, context: Context, checkBox: CheckBox) {
        try {
            val cal = Calendar.getInstance()

            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                try {
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    hours = hour
                    minutes = minute
                    textView.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)
                    textView.visibility = View.VISIBLE
                } catch (t: Throwable) {
                    logAndToast(t)
                }
            }
            val timePickerDialog = TimePickerDialog(
                context,R.style.date_picker, timeSetListener, cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), true
            )

            timePickerDialog.setOnCancelListener {
                try{
                if (textView.visibility == View.GONE) checkBox.isChecked = false
                } catch (t: Throwable) {
                    logAndToast(t)
                }
            }

            textView.setOnClickListener {
                try {
                    timePickerDialog.show()
                } catch (t: Throwable) {
                    logAndToast(t)
                }
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                try {
                    if (isChecked) {
                        if (textView.text.isEmpty())
                            timePickerDialog.show()
                        else
                            textView.visibility = View.VISIBLE
                    } else textView.visibility = View.GONE
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