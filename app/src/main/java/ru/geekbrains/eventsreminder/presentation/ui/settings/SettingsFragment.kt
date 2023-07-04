package ru.geekbrains.eventsreminder.presentation.ui.settings

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
import ru.geekbrains.eventsreminder.presentation.ui.toInt
import ru.geekbrains.eventsreminder.presentation.ui.toLocalTime
import java.time.LocalTime
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
                findPreference<WidgetPreviewPreference>(getString(R.string.key_widget_appearance_by_default))?.renew()
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
            initWidgetPreview()
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
                savePrefs.launch("EventsReminder.settings")
                true
            }
            val importSettingsButton: Preference? =
                findPreference(getString(R.string.key_import_settings_preference))
            importSettingsButton?.setOnPreferenceClickListener {
                loadPrefs.launch(arrayOf("*/*"))
                true
            }
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    private val savePrefs = registerForActivityResult(
        ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        try {
            uri?.let {
                if (Exportsettings.saveSharedPreferencesToFile(
                        uri,
                        requireContext(),
                        prefs
                    )
                ) Toast.makeText(context, getString(R.string.toast_msg_current_settings_saved), Toast.LENGTH_SHORT).show()
            }
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    private val loadPrefs = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        try {
            uri?.let {
                if (Exportsettings.loadSharedPreferencesFromFile(
                        uri,
                        requireContext(),
                        prefs
                    )
                ) findNavController().navigate(R.id.action_settings_self).also {
                    Toast.makeText(context, getString(R.string.toast_msg_settings_load_successfully), Toast.LENGTH_SHORT).show()
                }
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

    private fun initWidgetPreview() {
        try {
            findPreference<WidgetPreviewPreference>(getString(R.string.key_widget_appearance_by_default))?.applySettings(
                settingsData
            )
        } catch (t: Throwable) {
            outputError(t)
        }
    }

    private fun initNotificationPrefs() {
        try {
            val chooseNotificationStartTimeButton: Preference? =
                findPreference(getString(R.string.key_notification_start_time_preference))
            val time = prefs.getInt(
                getString(R.string.key_notification_start_time_preference),
                settingsData.timeToStartNotification
            ).toLocalTime()

            chooseNotificationStartTimeButton?.let {
                it.summary = time.toString()
                it.setOnPreferenceClickListener {
                    initTimePicker(
                        requireContext(), time,
                        chooseNotificationStartTimeButton
                    )
                    true
                }
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

    private fun initTimePicker(context: Context, curTime: LocalTime, preference: Preference) {
        try {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                try {
                    val time = LocalTime.of(hour, minute)
                    preference.summary = time.toString()
                    prefs.edit().putInt(
                        getString(R.string.key_notification_start_time_preference),
                        time.toInt()
                    ).apply()
                } catch (t: Throwable) {
                    logAndToast(t)
                }
            }
            val timePickerDialog = TimePickerDialog(
                context, R.style.date_picker, timeSetListener, curTime.hour,
                curTime.minute, true
            )
            timePickerDialog.show()

        } catch (t: Throwable) {
            logAndToast(t)
        }
    }

    private fun logAndToast(t: Throwable) = logAndToast(t, this::class.java.toString())

    private fun logAndToast(t: Throwable, tag: String?) {
        try {
            Log.e(tag, "", t)
            Toast.makeText(requireContext().applicationContext, t.toString(), Toast.LENGTH_LONG)
                .show()
        } catch (_: Throwable) {
        }
    }
}