package ru.geekbrains.eventsreminder.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import ru.geekbrains.eventsreminder.App
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidInjectionModule::class,
        EventsReminderDataModule::class,
        SettingsModule::class,
        ViewModelFactoryModule::class,
        UIModule::class,
        WidgetModule::class
    ]
)
interface EventsReminderComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun withContext(context: Context): Builder

        fun widgetModule(widgetModule: WidgetModule): Builder

        fun build(): EventsReminderComponent

    }
}
