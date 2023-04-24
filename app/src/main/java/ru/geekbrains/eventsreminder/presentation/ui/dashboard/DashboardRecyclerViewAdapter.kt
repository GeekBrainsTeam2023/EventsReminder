package ru.geekbrains.eventsreminder.presentation.ui.dashboard

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import ru.geekbrains.eventsreminder.R.layout.dashboard_recyclerview_item
import ru.geekbrains.eventsreminder.domain.EventData
import java.lang.reflect.Type
import java.time.LocalDate

class DashboardRecyclerViewAdapter(var events: List<EventData> ) :
    RecyclerView.Adapter<DashboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder =
        DashboardViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(dashboard_recyclerview_item, parent, false)
        )
            .also { it.createLifecycle() }

    override fun getItemCount(): Int = events.size
//    override fun getItemViewType(position: Int): Int {
//        var index = 0
//        var totalCount = 0
//        var prevCount = 0
//        do{
//            prevCount = totalCount
//            totalCount += events.values.toList()[index].size + 1
//            index++
//        }while (position > totalCount)
//
//            when (position - prevCount) {
//                0 -> return 0
//                else -> return 1
//            }
//    }

//    fun getEventAt(position :Int) : EventData{
//        var index = 0
//        var totalCount = 0
//        var prevCount = 0
//        var curCount = 0
//        do{
//            curCount = events.values.toList()[index].size
//            prevCount = totalCount
//            totalCount += curCount
//            index++
//        }while (position > totalCount + index)
//        if (position == 0) {
//            prevCount = -1
//        }
//        return events.values.toList()[index - 1][position - prevCount - index]
//
//    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        holder.bind(events[position],
            position == 0 || events[position - 1].date != events[position].date)
    }

    override fun onViewAttachedToWindow(holder: DashboardViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.attachToWindow()
    }

    override fun onViewDetachedFromWindow(holder: DashboardViewHolder) {
        holder.detachFromWindow()
        super.onViewDetachedFromWindow(holder)
    }
}

data class Event(
    var id: Int,
    var type: String,
    var title: String,
    var image: String,
    var eventDate: String,
    var daysBeforeEvent: String
)