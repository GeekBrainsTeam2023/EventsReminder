package ru.geekbrains.eventsreminder.repo.local

import android.util.Log
import ru.geekbrains.eventsreminder.domain.EventData
import javax.inject.Inject

class LocalRepoImp @Inject constructor(
    private val localEventsDB: LocalEventsDB
):LocalRepo {
    override fun addEvent(event: EventData) =
        try{
        localEventsDB
            .getLocalEventsDao()
            .insert(LocalEvent.fromEventData(event))
        }catch (t:Throwable){logErr(t)}
//    override fun updateEvent(event: EventData) {
//        localEventsDB
//            .getLocalEventsDao()
//            .update(LocalEvent.fromEventData(event))
//    }
    override fun deleteEvent(event: EventData) {
        try {
            localEventsDB.getLocalEventsDao()
                .delete(LocalEvent.fromEventData(event))
        }catch (t:Throwable){logErr(t)}
    }

//    override fun getEvent(sourceId: Long) =
//        localEventsDB.getLocalEventsDao()
//            .getLocalEvent(sourceId).toEventData()

    override fun clear() {
        try {
            localEventsDB.getLocalEventsDao().clear()
        }catch (t:Throwable){logErr(t)}
    }
    override fun getList(): List<EventData> {
        try {
            return localEventsDB
                .getLocalEventsDao()
                .getLocalEvents()
                .map { event -> event.toEventData() }
        } catch (t: Throwable) {
            logErr(t)
            return listOf()
        }
    }
    private fun logErr(t: Throwable) = logErr(t, this::class.java.toString())

    private fun logErr(t: Throwable, TAG: String) {
        try {
            Log.e(TAG, "", t)
        } catch (_: Throwable) {
        }
    }
}