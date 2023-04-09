package ru.geekbrains.eventsreminder.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.geekbrains.eventsreminder.MainActivity
import ru.geekbrains.eventsreminder.ui.dashboard.DashboardFragment
import ru.geekbrains.eventsreminder.ui.notifications.NotificationsFragment
import ru.geekbrains.eventsreminder.ui.settings.SettingsFragment

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
}