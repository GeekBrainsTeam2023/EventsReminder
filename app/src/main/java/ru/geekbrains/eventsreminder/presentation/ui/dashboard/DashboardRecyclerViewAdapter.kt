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
