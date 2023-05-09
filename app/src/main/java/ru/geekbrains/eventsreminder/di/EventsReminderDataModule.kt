package ru.geekbrains.eventsreminder.di

import android.content.ContentProvider
import android.content.Context
import dagger.Binds
import dagger.Module
import ru.geekbrains.eventsreminder.repo.Repo
import ru.geekbrains.eventsreminder.repo.RepoImpl
import ru.geekbrains.eventsreminder.repo.cache.CacheRepo
import ru.geekbrains.eventsreminder.repo.cache.CacheRepoImpl
import ru.geekbrains.eventsreminder.repo.local.LocalRepo
import ru.geekbrains.eventsreminder.repo.local.LocalRepoImp
import ru.geekbrains.eventsreminder.repo.remote.IPhoneCalendarRepo
import ru.geekbrains.eventsreminder.repo.remote.PhoneCalendarRepoImpl
import ru.geekbrains.eventsreminder.repo.remote.PhoneContactsRepo
import ru.geekbrains.eventsreminder.repo.remote.PhoneContactsRepoImpl

/*
* предоставляет возможность работать с Repo, IPhoneCalendarRepo и
* PhoneContactsRepo
* */
@Module
interface EventsReminderDataModule {

    @Binds
    fun bindLocalRepo(
        localRepo: LocalRepoImp
    ): LocalRepo

    @Binds
    fun bindCacheRepo(
        caheRepo : CacheRepoImpl
    ): CacheRepo

    @Binds
    fun bindIPhoneCalendarRepo(
        iPhoneCalendarRepo: PhoneCalendarRepoImpl
    ): IPhoneCalendarRepo

    @Binds
    fun bindPhoneContactsRepo(
        phoneContactsRepo: PhoneContactsRepoImpl
    ): PhoneContactsRepo

    @Binds
    fun bindsRepo(
        repo : RepoImpl
    ): Repo
}
