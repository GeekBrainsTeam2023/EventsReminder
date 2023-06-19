package ru.geekbrains.eventsreminder.di


import dagger.Binds
import dagger.Module
import ru.geekbrains.eventsreminder.repo.Repo
import ru.geekbrains.eventsreminder.repo.RepoImpl
import ru.geekbrains.eventsreminder.repo.cache.CacheRepo
import ru.geekbrains.eventsreminder.repo.cache.CacheRepoImpl
import ru.geekbrains.eventsreminder.repo.local.LocalRepo
import ru.geekbrains.eventsreminder.repo.local.LocalRepoImp
import ru.geekbrains.eventsreminder.repo.remote.PhoneCalendarRepo
import ru.geekbrains.eventsreminder.repo.remote.PhoneCalendarRepoImpl
import ru.geekbrains.eventsreminder.repo.remote.PhoneContactsRepo
import ru.geekbrains.eventsreminder.repo.remote.PhoneContactsRepoImpl

/*
* предоставляет возможность работать с Repo, IPhoneCalendarRepo и
* PhoneContactsRepo
* */
@Module(includes = [StorageModule::class])
interface EventsReminderDataModule {

    @Binds
    fun bindLocalRepo(
        localRepo: LocalRepoImp
    ): LocalRepo

    @Binds
    fun bindCacheRepo(
        cacheRepo : CacheRepoImpl
    ): CacheRepo

    @Binds
    fun bindPhoneCalendarRepo(
        iPhoneCalendarRepo: PhoneCalendarRepoImpl
    ): PhoneCalendarRepo

    @Binds
    fun bindPhoneContactsRepo(
        phoneContactsRepo: PhoneContactsRepoImpl
    ): PhoneContactsRepo

    @Binds
    fun bindsRepo(
        repo : RepoImpl
    ): Repo


}
