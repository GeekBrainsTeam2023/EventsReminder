package ru.geekbrains.eventsreminder.presentation.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.*
import com.rarepebble.colorpicker.ColorPreference
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.presentation.MainActivity
import ru.geekbrains.eventsreminder.presentation.ui.FontSizeSeekBarPreference
import javax.inject.Inject


class SettingsFragment : PreferenceFragmentCompat(), HasAndroidInjector {
    @Inject
    lateinit var settingsData: SettingsData
    private val prefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
    }
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    override fun onAttach(context: Context) {
        try {
            AndroidSupportInjection.inject(this as Fragment)
            super.onAttach(context)
            prefs.registerOnSharedPreferenceChangeListener(bindPreferenceSummaryToValueListener)
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        try {
            if (preference is ColorPreference) {
                preference.showDialog(this, 0)
            } else super.onDisplayPreferenceDialog(preference)
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, key: String?) {
        try {
            setPreferencesFromResource(R.xml.preferences, key)
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    private val bindPreferenceSummaryToValueListener =
        SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
            try {
                (requireActivity() as MainActivity).setPreferences(preferences, key)
            } catch (t: Throwable) {
                outputError(t)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            initPreferences()
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    override fun onDetach() {
        super.onDetach()
        try {
            val prefs =
                PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
            prefs.unregisterOnSharedPreferenceChangeListener(bindPreferenceSummaryToValueListener)
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    private fun initPreferences() {
        try {
            initDataSourcesPrefs()
            initNotificationPrefs()
            initWidgetDataPrefs()
            initWidgetLookFontPrefs()
            initWidgetLookBackgroundPrefs()
            initSettingsExportImportPrefs()
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    private fun initSettingsExportImportPrefs() {
        try {
            val exportSettingsButton: Preference? =
                findPreference(getString(R.string.key_export_settings_preference))
            exportSettingsButton?.setOnPreferenceClickListener {
                Toast.makeText(context, "export current settings", Toast.LENGTH_SHORT).show()
                true
            }
            val importSettingsButton: Preference? =
                findPreference(getString(R.string.key_import_settings_preference))
            importSettingsButton?.setOnPreferenceClickListener {
                Toast.makeText(context, "import settings", Toast.LENGTH_SHORT).show()
                true
            }
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    private fun initWidgetLookBackgroundPrefs() {
        try {
            val chooseWidgetBackgroundAltColor: ColorPreference? =
                findPreference(getString(R.string.key_background_alternating_color_preference)) as ColorPreference?
            chooseWidgetBackgroundAltColor?.setDefaultValue(settingsData.alternatingColorWidget)
            val chooseWidgetBackgroundColor: ColorPreference? =
                findPreference(getString(R.string.key_background_color_preference)) as ColorPreference?
            chooseWidgetBackgroundColor?.setDefaultValue(settingsData.colorWidget)
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    private fun initWidgetLookFontPrefs() {
        try {
            val chooseWidgetFontSize: FontSizeSeekBarPreference? =
                findPreference(getString(R.string.key_widget_font_size_preference))
            chooseWidgetFontSize?.value = prefs.getInt(
                getString(R.string.key_widget_font_size_preference),
                settingsData.sizeFontWidget
            )
            chooseWidgetFontSize?.setOnPreferenceClickListener {
                Toast.makeText(
                    context,
                    getString(R.string.widget_fontsize_toast) + chooseWidgetFontSize.value.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
            val chooseWidgetBirthdayFontColor: ColorPreference? =
                findPreference(getString(R.string.key_widget_birthday_font_color_preference)) as ColorPreference?
            chooseWidgetBirthdayFontColor?.setDefaultValue(settingsData.colorBirthdayFontWidget)
            val chooseWidgetHolidayFontColor: ColorPreference? =
                findPreference(getString(R.string.key_widget_holiday_font_color_preference)) as ColorPreference?
            chooseWidgetHolidayFontColor?.setDefaultValue(settingsData.colorHolidayFontWidget)
            val chooseWidgetSimpleEventFontColor: ColorPreference? =
                findPreference(getString(R.string.key_widget_simple_event_font_color_preference)) as ColorPreference?
            chooseWidgetSimpleEventFontColor?.setDefaultValue(settingsData.colorSimpleEventFontWidget)
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    private fun initWidgetDataPrefs() {
        try {
            findPreference<CheckBoxPreference>(getString(R.string.key_event_date_checkbox_preference))
                ?.isChecked = prefs.getBoolean(
                getString(R.string.key_event_date_checkbox_preference),
                settingsData.showDateEvent
            )
            findPreference<CheckBoxPreference>(getString(R.string.key_event_time_checkbox_preference))
                ?.isChecked = prefs.getBoolean(
                getString(R.string.key_event_time_checkbox_preference),
                settingsData.showTimeEvent
            )
            findPreference<CheckBoxPreference>(getString(R.string.key_age_checkbox_preference))
                ?.isChecked =
                prefs.getBoolean(
                    getString(R.string.key_age_checkbox_preference),
                    settingsData.showAge
                )

            findPreference<SeekBarPreference>(getString(R.string.key_widget_interval_of_events_preference))?.let {
                it.value =
                    prefs.getInt(
                        getString(R.string.key_widget_interval_of_events_preference),
                        settingsData.daysForShowEventsWidget
                    )
                it.seekBarIncrement = 1
            }
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    private fun initNotificationPrefs() {
        try {
            val chooseNotificationStartTimeButton: Preference? =
                findPreference(getString(R.string.key_notification_start_time_preference))
            chooseNotificationStartTimeButton?.setOnPreferenceClickListener {
                Toast.makeText(context, "time picker will be here", Toast.LENGTH_SHORT).show()
                true
            }
            val chooseNotificationMelodyButton: Preference? =
                findPreference(getString(R.string.key_notification_melody_preference))
            chooseNotificationMelodyButton?.setOnPreferenceClickListener {
                Toast.makeText(context, "choose melody", Toast.LENGTH_SHORT).show()
                true
            }
            findPreference<SeekBarPreference>(getString(R.string.key_minutes_before_notification_preference))?.let {
                it.value =
                    prefs.getInt(
                        getString(R.string.key_minutes_before_notification_preference),
                        settingsData.minutesForStartNotification
                    )
                it.seekBarIncrement = 1
            }
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    private fun initDataSourcesPrefs() {
        try {
            findPreference<CheckBoxPreference>(getString(R.string.key_phonebook_datasource_checkbox_preference))?.isChecked =
                prefs.getBoolean(
                    getString(R.string.key_phonebook_datasource_checkbox_preference),
                    settingsData.isDataContact
                )
            findPreference<CheckBoxPreference>(getString(R.string.key_calendar_datasource_checkbox_preference))?.isChecked =
                prefs.getBoolean(
                    getString(R.string.key_calendar_datasource_checkbox_preference),
                    settingsData.isDataCalendar
                )
            findPreference<SeekBarPreference>(getString(R.string.key_show_events_interval_preference))?.let {
                it.value =
                    prefs.getInt(
                        getString(R.string.key_show_events_interval_preference),
                        settingsData.daysForShowEvents
                    )
                it.seekBarIncrement = 1
            }
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    private fun outputError(t: Throwable) {
        try {
            Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show()
            Log.e(this::class.java.toString(), "", t)
        } catch (_: Throwable) {
        }
    }
}