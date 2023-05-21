package ru.geekbrains.eventsreminder.presentation

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import dagger.android.support.DaggerAppCompatActivity
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.databinding.ActivityMainBinding
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.service.NotificationService
import ru.geekbrains.eventsreminder.usecases.EVENTS_DATA
import ru.geekbrains.eventsreminder.usecases.MINUTES_FOR_START_NOTIFICATION
import ru.geekbrains.eventsreminder.widget.AppWidget
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var doubleBackToExitPressedOnce = false
    private lateinit var navController: NavController
    @Inject
    lateinit var settings: SettingsData

    companion object {
        const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isParamsSetRequired =
            !setPreferences(PreferenceManager.getDefaultSharedPreferences(applicationContext))
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeToDashboard, R.id.notifications, R.id.settings,
                R.id.chooseNewEventTypeDialog, R.id.createBirthdayDialog,
                R.id.createHolidayDialog
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        if (isParamsSetRequired)
            navController.navigate(R.id.settings)
        startService(Intent(this, NotificationService::class.java).apply {
            putExtra(MINUTES_FOR_START_NOTIFICATION, settings.minutesForStartNotification)
            }
        )

}
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        try {
            NavigationUI.onNavDestinationSelected(item, navController)
            super.onOptionsItemSelected(item)
        } catch (t: Throwable) {
            Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_SHORT).show()
            false
        }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        try {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            if (doubleBackToExitPressedOnce || navController.backQueue.count() > 2) {
                super.onBackPressed()
                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, getString(R.string.toast_msg_double_back_pressure_btn), Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed(
                { doubleBackToExitPressedOnce = false },
                2000
            )
        } catch (t: Throwable) {
            Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    private val calendarContactsPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        if (
            (!settings.isDataCalendar ||
                    map[Manifest.permission.READ_CALENDAR] == true) &&
            (!settings.isDataContact ||
                    map[Manifest.permission.READ_CONTACTS] == true)
        )
            initReminderRights()
        else showAskWhyDialog()
    }
    fun initReminderRights() {
        val rightsToDemand = mutableListOf<String>()
        if (settings.isDataCalendar)
            rightsToDemand.add(Manifest.permission.READ_CALENDAR)
        if (settings.isDataContact)
            rightsToDemand.add(Manifest.permission.READ_CONTACTS)
        if (rightsToDemand.any() && !checkPermission()) {
            calendarContactsPermission.launch(
                rightsToDemand.toTypedArray()
            )
        } else {
            Log.d(TAG, getString(R.string.log_msg_rights_check_succeeded))
            navController.navigate(R.id.homeToDashboard)
        }
    }
    fun checkPermission(): Boolean {
        return (!settings.isDataCalendar ||
                ContextCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.READ_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED) && (
                !settings.isDataContact ||
                        ContextCompat.checkSelfPermission(
                            applicationContext, Manifest.permission.READ_CONTACTS
                        ) == PackageManager.PERMISSION_GRANTED)
    }
    private fun showAskWhyDialog() {
        val builder = AlertDialog.Builder(this)
        val rightCalendarToDemand = if (settings.isDataCalendar) "календарю" else ""
        val rightContactToDemand = if (settings.isDataContact) "контактам" else ""
        val rightsToDemand = rightCalendarToDemand +
                (if (settings.isDataCalendar && settings.isDataContact) " и " else "") +
                rightContactToDemand
        builder.setTitle(getString(R.string.demands_dialog_title_first))
            .setMessage(buildString {
        append(getString(R.string.demands_dialog_title_second))
        append(rightsToDemand)
        append(getString(R.string.demands_dialog_title_third))
    })
            .setCancelable(false)
            .setPositiveButton("      права") { dialog, id ->
                // открываем настройки приложения, чтобы пользователь дал разрешение вручную
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                getPermissionManually.launch(intent)
            }
            .setNegativeButton("настройки     ") { dialog, id ->
                navController.navigate(R.id.settings)
            }
        val dlg = builder.create()
        dlg.show()
    }
    private val getPermissionManually = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        initReminderRights()
    }
    /**
     * Применить параметры из настроек
     * @param preferences набор настроек для применения в приложении
     * @param key ключ с названием конкретной настройки
     * @return true в случае успешного применения всех параметров
     *         false если требуется установка параметров
     * (в случае [null] - будут применены все настройки)
     * */
    fun setPreferences(preferences: SharedPreferences, key: String? = null): Boolean {
        var ret = false
        try {
            if (preferences.contains(getString(R.string.key_phonebook_datasource_checkbox_preference)))
              ret = true
            if (key.isNullOrBlank() || key == getString(R.string.key_phonebook_datasource_checkbox_preference)) {
                settings.isDataContact = preferences.getBoolean(
                    getString(R.string.key_phonebook_datasource_checkbox_preference),
                    settings.isDataContact
                )
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_calendar_datasource_checkbox_preference)) {
                settings.isDataCalendar = preferences.getBoolean(
                    getString(R.string.key_calendar_datasource_checkbox_preference),
                    settings.isDataCalendar
                )
            }
            if (key.isNullOrBlank() || key == (getString(R.string.key_show_events_interval_preference))) {
                settings.daysForShowEvents = preferences.getInt(
                    getString(R.string.key_show_events_interval_preference),
                    settings.daysForShowEvents
                )
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_event_date_checkbox_preference)) {
                settings.showDateEvent = preferences.getBoolean(
                    getString(R.string.key_event_date_checkbox_preference),
                    settings.showDateEvent
                )
                if (!key.isNullOrBlank()) {
                    runOnUiThread {
                        AppWidget.sendRefreshBroadcast(this)
                    }
                    return ret
                }
            }
            if(key.isNullOrBlank() || key == getString(R.string.key_event_time_checkbox_preference)){
                settings.showTimeEvent = preferences.getBoolean(getString(R.string.key_event_time_checkbox_preference),settings.showTimeEvent)
                if (!key.isNullOrBlank()) {
                    runOnUiThread {
                        AppWidget.sendRefreshBroadcast(this)
                    }
                    return ret
                }
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_widget_font_size_preference)) {
                settings.sizeFontWidget = preferences.getInt(
                    getString(R.string.key_widget_font_size_preference),
                    settings.sizeFontWidget
                )
                if (!key.isNullOrBlank()) {
                    runOnUiThread {
                        AppWidget.sendRefreshBroadcast(this)
                    }
                    return ret
                }
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_widget_birthday_font_color_preference)) {
                settings.colorBirthdayFontWidget = preferences.getInt(
                    getString(R.string.key_widget_birthday_font_color_preference),
                    settings.colorBirthdayFontWidget
                )
                if (!key.isNullOrBlank()) {
                    runOnUiThread {
                        AppWidget.sendRefreshBroadcast(this)
                    }
                    return ret
                }
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_widget_holiday_font_color_preference)) {
                settings.colorHolidayFontWidget = preferences.getInt(
                    getString(R.string.key_widget_holiday_font_color_preference),
                    settings.colorHolidayFontWidget
                )
                if (!key.isNullOrBlank()) {
                    runOnUiThread {
                        AppWidget.sendRefreshBroadcast(this)
                    }
                    return ret
                }
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_widget_simple_event_font_color_preference)) {
                settings.colorSimpleEventFontWidget = preferences.getInt(
                    getString(R.string.key_widget_simple_event_font_color_preference),
                    settings.colorSimpleEventFontWidget
                )
                if (!key.isNullOrBlank()) {
                    runOnUiThread {
                        AppWidget.sendRefreshBroadcast(this)
                    }
                    return ret
                }
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_background_color_preference)) {
                settings.colorWidget = preferences.getInt(
                    getString(R.string.key_background_color_preference),
                    settings.colorWidget
                )
                if (!key.isNullOrBlank()) {
                    runOnUiThread {
                        AppWidget.sendRefreshBroadcast(this)
                    }
                    return ret
                }
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_background_alternating_color_preference)) {
                settings.alternatingColorWidget = preferences.getInt(
                    getString(R.string.key_background_alternating_color_preference),
                    settings.alternatingColorWidget
                )
                if (!key.isNullOrBlank()) {
                    runOnUiThread {
                        AppWidget.sendRefreshBroadcast(this)
                    }
                    return ret
                }
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_widget_interval_of_events_preference)) {
                settings.daysForShowEventsWidget = preferences.getInt(getString(R.string.key_widget_interval_of_events_preference),settings.daysForShowEventsWidget)
                if (!key.isNullOrBlank()) {
                    runOnUiThread {
                        AppWidget.sendRefreshBroadcast(this)
                    }
                    return ret
                }
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_age_checkbox_preference)) {
                settings.showAge = preferences.getBoolean(getString(R.string.key_age_checkbox_preference),settings.showAge)
                if (!key.isNullOrBlank()) {
                    runOnUiThread {
                        AppWidget.sendRefreshBroadcast(this)
                    }
                    return ret
                }
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_minutes_before_notification_preference)) {
                settings.minutesForStartNotification = preferences.getInt(getString(R.string.key_minutes_before_notification_preference),settings.minutesForStartNotification)
                if (!key.isNullOrBlank()) {
                    startService(Intent(this, NotificationService::class.java).apply {
                        putExtra(MINUTES_FOR_START_NOTIFICATION, settings.minutesForStartNotification)
                    }
                    )
                    return ret
                }
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_export_settings_preference)) {
                //TODO: записать текущие настройки в файл
                if (!key.isNullOrBlank()) return ret
            }
            if (key.isNullOrBlank() || key == getString(R.string.key_import_settings_preference)) {
                //TODO: загрузить настройки из файла
                if (key.isNullOrBlank()) return ret
            }
        } catch (t: Throwable) {
            Toast.makeText(this, t.toString(), Toast.LENGTH_SHORT).show()
            return false
        }
        return ret
    }
}