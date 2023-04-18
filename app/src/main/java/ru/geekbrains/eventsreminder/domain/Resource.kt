package ru.geekbrains.eventsreminder.domain

sealed class ResourceState<T> {
    class SuccessState<T>(val data: T) : ResourceState<T>()
    class ErrorState<T>(val error: Throwable) : ResourceState<T>()
}
