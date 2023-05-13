package ru.geekbrains.eventsreminder.repo.local

import ru.geekbrains.eventsreminder.domain.EventData
import javax.inject.Inject

class LocalRepoImp @Inject constructor(
    private val localEventsDB: LocalEventsDB
):LocalRepo {

    override fun addEvent(event: EventData) =
        localEventsDB
            .getLocalEventsDao()
            .insert(LocalEvent.fromEventData(event))

    override fun updateEvent(event: EventData) {
        localEventsDB
            .getLocalEventsDao()
            .update(LocalEvent.fromEventData(event))
    }

    override fun deleteEvent(event: EventData) {
        localEventsDB.getLocalEventsDao()
            .delete(LocalEvent.fromEventData(event))
    }

    override fun getList(): List<EventData> =
        localEventsDB
            .getLocalEventsDao()
            .getLocalEvents()
            .map { event->event.toEventData() }
}