package ru.geekbrains.eventsreminder.di


import dagger.Module
import dagger.Provides
import ru.geekbrains.eventsreminder.domain.SettingsData
import javax.inject.Singleton

@Module
class SettingsModule {
    @Singleton
    @Provides
    fun provideSettings() = SettingsData()
}