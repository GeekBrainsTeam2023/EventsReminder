package ru.geekbrains.eventsreminder.presentation.ui.dashboard

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.opengl.Visibility
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.R.color.light_green
import ru.geekbrains.eventsreminder.databinding.DashboardRecyclerviewItemBinding
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view), LifecycleOwner {
    private val binding: DashboardRecyclerviewItemBinding by viewBinding()
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val activity = view.context.findActivity()
    private var paused: Boolean = false

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    fun createLifecycle() {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    fun attachToWindow() {
        if (paused) {
            lifecycleRegistry.currentState = Lifecycle.State.RESUMED
            paused = false
        } else {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }
    }

    fun detachFromWindow() {
        if (!paused) {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
            paused = true
        }
    }

/**
 * Привязать к вьюхолдеру конкретный EventData
 * @param item EvenData для привязки
 * @param isDataHeader Показывать ли в карточке Дату вверху карточки
 * */
    @RequiresApi(Build.VERSION_CODES.O)
    fun bind( item: EventData,isDataHeader :Boolean) {
        with(binding) {
            with(dashboardRecyclerViewCardview) {
                with(dashboardRecyclerViewItemImage) {
                    with(activity) {

                            val event = item
                            when (event.type) {
                                EventType.BIRTHDAY -> setCardBackgroundColor(
                                    resources.getColor(
                                        light_green,
                                        theme
                                    )
                                )
                                    .also { setImageResource(R.drawable.ic_home_24dp) }
                                EventType.HOLIDAY -> setCardBackgroundColor(
                                    resources.getColor(
                                        R.color.light_violet,
                                        theme
                                    )
                                )
                                    .also { setImageResource(R.drawable.ic_add_24) }
                                EventType.SIMPLE -> setCardBackgroundColor(
                                    resources.getColor(
                                        R.color.light_blue,
                                        theme
                                    )
                                ).also { setImageResource(R.drawable.ic_dashboard_black_24dp) }
                            }
                            dashboardRecyclerViewItemTitleTextview.text = event.name
                            dashboardRecyclerViewItemDaysBeforeEventTextview.text = dateToDaysInWords(event.date)
                           if(event.type == EventType.BIRTHDAY){
                               dashboardRecyclerViewItemEventDateTextview.text = event.birthday?.format(
                                   DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                           }
                           else {dashboardRecyclerViewItemEventDateTextview.text = event.date.format(
                                DateTimeFormatter.ofPattern("dd-MM-yyyy"))}
                            textViewDashboardIntervalOfEvents.visibility =
                                if (isDataHeader) {
                                    View.VISIBLE.also{
                                    textViewDashboardIntervalOfEvents.text =
                                        dashboardRecyclerViewItemDaysBeforeEventTextview.text
                                    }
                                } else View.GONE

                    }
                }
            }


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateToDaysInWords(date: LocalDate) =
        when (ChronoUnit.DAYS.between(LocalDate.now(), date).toInt()) {
            0 -> "Сегодня"
            1 -> "Завтра"
            2 -> "Послезавтра"
            else ->"через " + RusIntPlural(
                "д",
                ChronoUnit.DAYS.between(LocalDate.now(), date).toInt(),
                "ень", "ня", "ней"
            ).toString()
        }


    /**
     * Класс для вывода числительных с правильными окончаниями в зависимости от количества
     * */
    data class RusIntPlural(
        val name: String,
        val number: Int,
        val singleEnding: String = "",
        val twoToFourEnding: String = "",
        val fiveToTenEnding: String = ""
    ) {
        override fun toString(): String {
            var suffix = fiveToTenEnding
            if ((number / 10) % 10 != 1) {
                val num = number % 10
                if (num > 0 && num < 2) suffix = singleEnding
                else if (num > 1 && num < 5) suffix = twoToFourEnding
            }
            return "$number $name$suffix"
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