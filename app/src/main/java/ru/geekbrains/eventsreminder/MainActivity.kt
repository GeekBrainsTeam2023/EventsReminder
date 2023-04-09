package ru.geekbrains.eventsreminder

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import ru.geekbrains.eventsreminder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var doubleBackToExitPressedOnce = false
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(binding.root)
       navController =  findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeToDashboard, R.id.notifications, R.id.settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.overflow_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        NavigationUI.onNavDestinationSelected(item, navController )
        return super.onOptionsItemSelected(item)
    }

    /**
     * Применить параметры из настроек
     * @param preferences набор настроек для применения в приложении
     * @param key ключ с названием конкретной настройки
     * (в случае [null] - будут применены все настройки)
     * */
    fun setPreferences(preferences: SharedPreferences, key: String? = null){
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

            if (key.isNullOrBlank() || key == getString(R.string.key_widget_size_list_preference)) {
                // TODO: установить размер виджета в соотвествтвующей вьюмодели
                if (!key.isNullOrBlank()) return
            }

            if (key.isNullOrBlank() || key == getString(R.string.key_widget_size_list_preference)) {
                // TODO: установить размер виджета в соотвествтвующей вьюмодели
                if (!key.isNullOrBlank()) return
            }


        } catch (t: Throwable) {
            Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_SHORT).show()
        }
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
            Toast.makeText(this, "Нажмите НАЗАД для выхода", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed(
                { doubleBackToExitPressedOnce = false },
                2000
            )

        } catch (t: Throwable) {
            Toast.makeText(applicationContext,t.toString(), Toast.LENGTH_SHORT).show()
        }
    }

}