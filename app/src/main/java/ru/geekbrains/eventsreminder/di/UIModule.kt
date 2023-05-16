package ru.geekbrains.eventsreminder.di


import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.geekbrains.eventsreminder.presentation.MainActivity
import ru.geekbrains.eventsreminder.presentation.ui.dashboard.DashboardFragment
import ru.geekbrains.eventsreminder.presentation.ui.dialogs.CreateSimpleEventDialogFragment
import ru.geekbrains.eventsreminder.presentation.ui.dialogs.CreateBirthdayEventDialogFragment
import ru.geekbrains.eventsreminder.presentation.ui.dialogs.CreateHolidayEventDialogFragment
import ru.geekbrains.eventsreminder.presentation.ui.dialogs.CreateNewEventDialogFragment
import ru.geekbrains.eventsreminder.presentation.ui.notifications.NotificationsFragment
import ru.geekbrains.eventsreminder.presentation.ui.settings.SettingsFragment

@Module
interface UIModule {
    @ContributesAndroidInjector
    fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    fun bindDashboardFragment(): DashboardFragment

    @ContributesAndroidInjector
    fun bindSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    fun bindNotificationsFragment(): NotificationsFragment

    @ContributesAndroidInjector
    fun bindCreateNewEventDialogFragment(): CreateNewEventDialogFragment

    @ContributesAndroidInjector
    fun bindCreateBirthdayEventDialogFragment(): CreateBirthdayEventDialogFragment
    @ContributesAndroidInjector
    fun bindCreateHolidayEventDialogFragment(): CreateHolidayEventDialogFragment
    @ContributesAndroidInjector
    fun bindCreateAnotherEventTypeDialogFragment(): CreateSimpleEventDialogFragment
    // annotation class ForApplication
}