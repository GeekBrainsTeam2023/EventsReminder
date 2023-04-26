package ru.geekbrains.eventsreminder.di

import android.app.Application
import android.content.Context
import ru.geekbrains.eventsreminder.domain.SettingsData
import ru.geekbrains.eventsreminder.presentation.ui.settings.SettingsFragment
import ru.geekbrains.eventsreminder.repo.Repo
import ru.geekbrains.eventsreminder.repo.RepoImpl
import ru.geekbrains.eventsreminder.repo.local.LocalRepo
import ru.geekbrains.eventsreminder.repo.local.LocalRepoImp
import ru.geekbrains.eventsreminder.repo.remote.IPhoneCalendarRepo
import ru.geekbrains.eventsreminder.repo.remote.PhoneCalendarRepoImpl
import ru.geekbrains.eventsreminder.repo.remote.PhoneContactsRepo
import ru.geekbrains.eventsreminder.repo.remote.PhoneContactsRepoImpl

// TODO: This classes are to make everyone understand how it will work with dagger

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    // TODO: DONT USE IT IN PRODUCTION! REMOVE MEMORYLEAK WITH HELP OF CORRECT DI!
    companion object {
        var context: Context? = null
    }
}

class IPhoneCalendarRepoFactory{
    companion object{
        private var iPhoneCalendarRepo : IPhoneCalendarRepo? = null
        fun getIPhoneCalendarRepo() = iPhoneCalendarRepo ?: PhoneCalendarRepoImpl(App.context!!)
    }
}

class PhoneContactsRepoFactory{
    companion object{
        private var phoneContactsRepo : PhoneContactsRepo? = null
        fun getPhoneContactsRepo() = phoneContactsRepo ?: PhoneContactsRepoImpl(App.context!!)
    }
}

class LocalRepoFactory{
    companion object{
        private var localRepo : LocalRepo? = null
        fun getLocalRepo() = localRepo ?: LocalRepoImp()
    }
}

class SettingsDataFactory{
    companion object{
        private var settingsData = SettingsData()
        fun getSettingsData() = settingsData
    }
}

class RepoFactory{
    companion object{
        private var repo : Repo? = null
        fun getRepo() = repo ?: RepoImpl(
            LocalRepoFactory.getLocalRepo(),

        PhoneContactsRepoFactory.getPhoneContactsRepo(),
        IPhoneCalendarRepoFactory.getIPhoneCalendarRepo())
    }
}


class InsteadOfDaggerFactories {
}