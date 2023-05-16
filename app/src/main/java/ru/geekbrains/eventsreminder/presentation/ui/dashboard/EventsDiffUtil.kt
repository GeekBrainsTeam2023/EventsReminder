package ru.geekbrains.eventsreminder.presentation.ui.dashboard

import androidx.recyclerview.widget.DiffUtil
import ru.geekbrains.eventsreminder.domain.EventData

class EventsDiffUtil(private val oldList: List<EventData>, private val newList: List<EventData>) :
    DiffUtil.Callback() {
    private val payload = Any()
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return try {
            oldList[oldItemPosition].hashCode() == newList[newItemPosition].hashCode()
        } catch (_: Throwable) {
            false
        }
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return try {
            val oldItem: EventData = oldList[oldItemPosition]
            val newItem: EventData = newList[newItemPosition]
            oldItem.name == newItem.name &&
                    oldItem.date == newItem.date &&
                    oldItem.time == newItem.time &&
                    oldItem.type == newItem.type
        } catch (_: Throwable) {
            false
        }
    }
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int) = payload
}