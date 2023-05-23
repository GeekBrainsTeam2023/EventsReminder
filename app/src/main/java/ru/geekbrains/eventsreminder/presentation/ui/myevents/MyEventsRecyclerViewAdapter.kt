package ru.geekbrains.eventsreminder.presentation.ui.myevents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.domain.EventData

class MyEventsRecyclerViewAdapter(var myEvents: List<EventData>,
                                  val viewModel: MyEventsViewModel
): RecyclerView.Adapter<MyEventsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEventsViewHolder =
        MyEventsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.my_events_recyclerview_item, parent, false)
        )
            .also { it.createLifecycle() }

    override fun getItemCount(): Int = myEvents.size

    override fun onBindViewHolder(holder: MyEventsViewHolder, position: Int) {
            holder.bind(myEvents[position],
                position == 0 || myEvents[position - 1].date != myEvents[position].date, viewModel)
        }
}