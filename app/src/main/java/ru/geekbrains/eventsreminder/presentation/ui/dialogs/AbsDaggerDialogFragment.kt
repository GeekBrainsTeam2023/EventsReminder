package ru.geekbrains.eventsreminder.presentation.ui.dialogs

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerDialogFragment
import ru.geekbrains.eventsreminder.di.ViewModelFactory
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.presentation.ui.SUCCESS_ID_TO_NAVIGATE
import ru.geekbrains.eventsreminder.presentation.ui.dashboard.DashboardViewModel
import ru.geekbrains.eventsreminder.presentation.ui.parcelable
import javax.inject.Inject

abstract class AbsDaggerDialogFragment: DaggerDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    protected val dashboardViewModel by viewModels<DashboardViewModel>({ this }) { viewModelFactory }
    @Inject
    lateinit var settings: SettingsData
    protected var successIdToNavigate : Int? = null
    var eventData: EventData? = null
    protected fun processBundleArguments() {
        try{
        arguments?.parcelable<EventData>(EventData::class.toString())?.let { eventData = it }
        arguments?.getInt(SUCCESS_ID_TO_NAVIGATE)?.let { successIdToNavigate = it }
        } catch (t: Throwable) {
            logAndToast(t)
        }
    }
    protected fun navigateOnSuccess() {
        try{
        if (successIdToNavigate != null && successIdToNavigate != 0)
            findNavController().navigate(successIdToNavigate!!)
        else
            findNavController().navigateUp()
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