package ru.geekbrains.eventsreminder.presentation.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.di.SettingsDataFactory
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.presentation.MainActivity

class SettingsFragment(
) :
    PreferenceFragmentCompat() {//, HasAndroidInjector { //это заготовка для даггера
    private val settingsData = SettingsDataFactory.getSettingsData()
    //lateinit var androidInjector: DispatchingAndroidInjector<Any>
    override fun onAttach(context: Context) {
        try {
            //AndroidSupportInjection.inject(this as Fragment)
            super.onAttach(context)
            val prefs =
                PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
            prefs.registerOnSharedPreferenceChangeListener(bindPreferenceSummaryToValueListener)
        } catch (t: Throwable) {
            Toast.makeText(context, "failed to get preferences", Toast.LENGTH_SHORT).show()
        }
    }

    //    override fun androidInjector(): AndroidInjector<Any> {
//        return androidInjector
//    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, key: String?) {

        setPreferencesFromResource(R.xml.preferences, key)
    }

    val bindPreferenceSummaryToValueListener =
        SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
            setPreferences(preferences, key)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPreferenceButtons()
    }

    override fun onDetach() {
        super.onDetach()
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
        prefs.unregisterOnSharedPreferenceChangeListener(bindPreferenceSummaryToValueListener)
    }

    private fun initPreferenceButtons() {
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
        val chooseMinutesBeforeEventToStartNotificationButton: Preference? =
            findPreference(getString(R.string.key_minutes_before_notification_preference))
        chooseMinutesBeforeEventToStartNotificationButton?.setOnPreferenceClickListener {
            Toast.makeText(
                context,
                "choose minutes before event to start notification",
                Toast.LENGTH_SHORT
            ).show()
            true
        }

        val chooseDaysBeforeEventToStartNotificationButton: Preference? =
            findPreference(getString(R.string.key_days_before_notification_preference))
        chooseDaysBeforeEventToStartNotificationButton?.setOnPreferenceClickListener {
            Toast.makeText(
                context,
                "choose days before event to start notification",
                Toast.LENGTH_SHORT
            ).show()
            true
        }

        val chooseWidgetFontSizeButton: Preference? =
            findPreference(getString(R.string.key_widget_font_size_preference))
        chooseWidgetFontSizeButton?.setOnPreferenceClickListener {
            Toast.makeText(context, "choose widget font size", Toast.LENGTH_SHORT).show()
            true
        }
        val chooseWidgetBackgroundColorButton: Preference? =
            findPreference(getString(R.string.key_background_color_preference))
        chooseWidgetBackgroundColorButton?.setOnPreferenceClickListener {
            Toast.makeText(context, "choose widget background color", Toast.LENGTH_SHORT).show()
            true
        }
        val chooseWidgetBackgroundTransparencyButton: Preference? =
            findPreference(getString(R.string.key_background_transparency_preference))
        chooseWidgetBackgroundTransparencyButton?.setOnPreferenceClickListener {
            Toast.makeText(context, "choose widget background transparency", Toast.LENGTH_SHORT)
                .show()
            true
        }
        val chooseWidgetBorderThicknessButton: Preference? =
            findPreference(getString(R.string.key_widget_border_thickness_preference))
        chooseWidgetBorderThicknessButton?.setOnPreferenceClickListener {
            Toast.makeText(context, "choose widget border thickness", Toast.LENGTH_SHORT).show()
            true
        }
        val chooseWidgetBorderColorButton: Preference? =
            findPreference(getString(R.string.key_widget_border_color_preference))
        chooseWidgetBorderColorButton?.setOnPreferenceClickListener {
            Toast.makeText(context, "choose widget border color", Toast.LENGTH_SHORT).show()
            true
        }
        val chooseWidgetBorderRoundedCornersButton: Preference? =
            findPreference(getString(R.string.key_widget_border_rounded_corners_preference))
        chooseWidgetBorderRoundedCornersButton?.setOnPreferenceClickListener {
            Toast.makeText(context, "choose widget border rounded corners", Toast.LENGTH_SHORT)
                .show()
            true
        }

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

    }
    /**
     * Применить параметры из настроек
     * @param preferences набор настроек для применения в приложении
     * @param key ключ с названием конкретной настройки
     * (в случае [null] - будут применены все настройки)
     * */
    fun setPreferences(preferences: SharedPreferences, key: String? = null) {
        try {

            if (key.isNullOrBlank() || key == getString(R.string.key_notification_start_time_preference)) {
                // TODO: установить время начала уведомления в соотвествтвующей вьюмодели
                if (!key.isNullOrBlank()) return
            }

            if (key.isNullOrBlank() || key == getString(R.string.key_event_date_checkbox_preference)) {
                // TODO: включить/выключить вывод даты события в соотвествтвующей вьюмодели
                if (!key.isNullOrBlank()) return
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_event_time_checkbox_preference)) {
                // TODO: включить/выключить вывод времени события в соотвествтвующей вьюмодели
                if (!key.isNullOrBlank()) return
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_age_checkbox_preference)) {
                // TODO: включить/выключить вывод возраста именинника в соотвествтвующей вьюмодели
                if (!key.isNullOrBlank()) return
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_birthdate_checkbox_preference)) {
                // TODO: включить/выключить вывод даты дня рожджения в соотвествтвующей вьюмодели
                if (!key.isNullOrBlank()) return
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_age_checkbox_preference)) {
                // TODO: включить/выключить вывод возраста именинника в соотвествтвующей вьюмодели
                if (!key.isNullOrBlank()) return
            }

            if (key.isNullOrBlank() || key == getString(R.string.key_widget_border_rounded_corners_preference)) {
                // TODO: установить скругление углов рамки виджета в соотвествтвующей вьюмодели
                if (!key.isNullOrBlank()) return
            }

            if (key.isNullOrBlank() || key == getString(R.string.key_widget_font_size_preference)) {
                // TODO: установить размер шрифта виджета в соотвествтвующей вьюмодели
                if (!key.isNullOrBlank()) return
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_export_settings_preference)) {
                //TODO: записать текущие настройки в файл
                if (key.isNullOrBlank()) return
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_import_settings_preference)) {
                //TODO: загрузить настройки из файла
                if (key.isNullOrBlank()) return
            }


        } catch (t: Throwable) {
            Toast.makeText(requireActivity(), t.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}