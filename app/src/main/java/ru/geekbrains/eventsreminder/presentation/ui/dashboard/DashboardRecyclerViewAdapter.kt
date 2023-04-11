package ru.geekbrains.eventsreminder.presentation.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.geekbrains.eventsreminder.R.layout.dashboard_recyclerview_item

class DashboardRecyclerViewAdapter(val eventsList: List<Event>) :
    RecyclerView.Adapter<DashboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder =
        DashboardViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(dashboard_recyclerview_item, parent, false)
        )
            .also { it.createLifecycle() }

    override fun getItemCount(): Int = eventsList.size

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        holder.bind(eventsList[position])
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