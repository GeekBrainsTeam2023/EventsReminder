package ru.geekbrains.eventsreminder.presentation.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.presentation.MainActivity

class SettingsFragment : PreferenceFragmentCompat(){//, HasAndroidInjector { //это заготовка для даггера

    //lateinit var androidInjector: DispatchingAndroidInjector<Any>
    override fun onAttach(context: Context) {
        try{
            //AndroidSupportInjection.inject(this as Fragment)
            super.onAttach(context)
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
            prefs.registerOnSharedPreferenceChangeListener(bindPreferenceSummaryToValueListener)
        } catch (t: Throwable){
            Toast.makeText(context,"failed to get preferences",Toast.LENGTH_SHORT).show()
        }
    }
//    override fun androidInjector(): AndroidInjector<Any> {
//        return androidInjector
//    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, key: String?) {
        setPreferencesFromResource(R.xml.preferences, key)
    }
    val bindPreferenceSummaryToValueListener = SharedPreferences.OnSharedPreferenceChangeListener{
        preferences, key ->
        (requireActivity() as MainActivity).setPreferences(preferences,key)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPreferenceButtons()
    }

    override fun onDetach() {
        super.onDetach()
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
        prefs.unregisterOnSharedPreferenceChangeListener(bindPreferenceSummaryToValueListener)
    }
    private fun initPreferenceButtons(){
        val notificationStartTimeButton : Preference? = findPreference(getString(R.string.key_notification_start_time_preference))
        notificationStartTimeButton?.setOnPreferenceClickListener {
            Toast.makeText(context,"time picker will be here", Toast.LENGTH_SHORT).show()
            true }
        val exportSettingsButton: Preference? = findPreference(getString(R.string.key_export_settings_preference))
        exportSettingsButton?.setOnPreferenceClickListener {
            Toast.makeText(context,"export current settings", Toast.LENGTH_SHORT).show()
true
        }
        val importSettingsButton: Preference? = findPreference(getString(R.string.key_import_settings_preference))
        importSettingsButton?.setOnPreferenceClickListener {
            Toast.makeText(context,"import settings", Toast.LENGTH_SHORT).show()
            true
        }
    }
}