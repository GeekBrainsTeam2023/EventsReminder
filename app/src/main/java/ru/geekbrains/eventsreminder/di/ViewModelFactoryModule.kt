package ru.geekbrains.eventsreminder.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.geekbrains.eventsreminder.presentation.ui.dashboard.DashboardViewModel
import ru.geekbrains.eventsreminder.presentation.ui.myevents.MyEventsViewModel

/*
* модуль предоставляет доступ к фабрике вьюмодели.
* */
@Module
interface ViewModelFactoryModule {
    @Binds
    fun bindsViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    fun bindDashboardViewModel(viewModel: DashboardViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(MyEventsViewModel::class)
    fun bindMyEventsViewModel(viewModel: MyEventsViewModel): ViewModel
}
