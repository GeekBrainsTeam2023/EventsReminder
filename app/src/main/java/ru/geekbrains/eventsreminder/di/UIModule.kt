package ru.geekbrains.eventsreminder.di


import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.geekbrains.eventsreminder.presentation.MainActivity
import ru.geekbrains.eventsreminder.presentation.ui.dashboard.DashboardFragment
import ru.geekbrains.eventsreminder.presentation.ui.dialogs.EditSimpleEventDialogFragment
import ru.geekbrains.eventsreminder.presentation.ui.dialogs.EditBirthdayEventDialogFragment
import ru.geekbrains.eventsreminder.presentation.ui.dialogs.EditHolidayEventDialogFragment
import ru.geekbrains.eventsreminder.presentation.ui.dialogs.CreateNewEventDialogFragment
import ru.geekbrains.eventsreminder.presentation.ui.myevents.MyEventsFragment
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
    fun bindMyEventsFragment(): MyEventsFragment

    @ContributesAndroidInjector
    fun bindCreateNewEventDialogFragment(): CreateNewEventDialogFragment

    @ContributesAndroidInjector
    fun bindCreateBirthdayEventDialogFragment(): EditBirthdayEventDialogFragment
    @ContributesAndroidInjector
    fun bindCreateHolidayEventDialogFragment(): EditHolidayEventDialogFragment
    @ContributesAndroidInjector
    fun bindCreateAnotherEventTypeDialogFragment(): EditSimpleEventDialogFragment
}