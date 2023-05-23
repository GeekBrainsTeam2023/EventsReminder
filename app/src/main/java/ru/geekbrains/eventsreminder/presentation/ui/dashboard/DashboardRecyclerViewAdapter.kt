package ru.geekbrains.eventsreminder.presentation.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.geekbrains.eventsreminder.R.layout.dashboard_recyclerview_item
import ru.geekbrains.eventsreminder.domain.EventData

class DashboardRecyclerViewAdapter(var events: List<EventData> ) :
    RecyclerView.Adapter<DashboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder =
        DashboardViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(dashboard_recyclerview_item, parent, false)
        )
            .also { it.createLifecycle() }
    override fun getItemCount(): Int = events.size
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        holder.bind(events[position],
            position == 0 || events[position - 1].date != events[position].date)
    }
}
