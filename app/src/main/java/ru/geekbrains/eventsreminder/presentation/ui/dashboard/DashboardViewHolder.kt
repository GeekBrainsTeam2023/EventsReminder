package ru.geekbrains.eventsreminder.presentation.ui.dashboard

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.databinding.DashboardRecyclerviewItemBinding

class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view), LifecycleOwner {
    private val binding: DashboardRecyclerviewItemBinding by viewBinding()
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val activity = view.context.findActivity()
    private var paused: Boolean = false
    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }
    fun createLifecycle(){
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }
//    override fun getLifecycle(): Lifecycle{
//        return lifecycleRegistry
//    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
    fun attachToWindow(){
        if(paused){
            lifecycleRegistry.currentState = Lifecycle.State.RESUMED
            paused = false
        } else {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }
    }
    fun detachFromWindow(){
        if(!paused){
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
            paused = true
        }
    }

    fun bind(event: Event){
        with(binding){
               dashboardRecyclerViewItemImage.setImageResource(R.drawable.ic_dashboard_black_24dp)
            dashboardRecyclerViewItemTitleTextview.text = event.title
            dashboardRecyclerViewItemDaysBeforeEventTextview.text = event.daysBeforeEvent
            dashboardRecyclerViewItemEventDateTextview.text = event.eventDate
    }

    }

    tailrec fun Context.findActivity(): Activity {
        if (this is Activity) {
            return this
        } else {
            if (this is ContextWrapper) {
                return this.baseContext.findActivity()
            }
            throw java.lang.IllegalStateException("Context chain has no activity")
        }
    }
}