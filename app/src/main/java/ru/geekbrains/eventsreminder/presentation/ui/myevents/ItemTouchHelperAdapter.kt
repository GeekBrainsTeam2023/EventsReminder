package ru.geekbrains.eventsreminder.presentation.ui.myevents

import ru.geekbrains.eventsreminder.domain.EventData

interface ItemTouchHelperAdapter {
    fun onItemDismiss(myEventsViewHolder: MyEventsViewHolder)
}