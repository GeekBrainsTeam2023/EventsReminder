package ru.geekbrains.eventsreminder.presentation.ui.dashboard


import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.databinding.FragmentDashboardBinding
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.domain.EventType
import ru.geekbrains.eventsreminder.domain.PeriodType
import ru.geekbrains.eventsreminder.presentation.MainActivity
import ru.geekbrains.eventsreminder.widget.AppWidget
import ru.geekbrains.eventsreminder.widget.Contract
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class DashboardFragment : Fragment() {

    private val binding: FragmentDashboardBinding by viewBinding()
    private var dashboardAdapter: DashboardRecyclerViewAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dashboard, container, false)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val event = EventData(
            EventType.BIRTHDAY,
            PeriodType.DAY,
            LocalDate.of(2000,3,22),
            LocalDate.of(2000,3,22),
            LocalTime.of(18,30,22),
            LocalTime.of(10,20,55),
            "Vasiliy Ivanovich Zagorulko")

        val event1 = Event(2, "Holiday","Holiday", "ic_notification_add_24.xml", "08.03", "3 days")
        val event2 = Event(3, "Birthday","Svetlana", "ic_notification_add_24", "11.06.2003", "10 days")
        val event3 =
            Event(4, "SimpleEvent","Visit to my dantist", "ic_notification_add_24.xml", "27.01.2020", "today")
        val eventsList: List<Event> = listOf(event1, event1, event2, event3)
        dashboardAdapter = DashboardRecyclerViewAdapter(eventsList)
        binding.recyclerViewListOfEvents.adapter = dashboardAdapter
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
//        val textView: TextView = binding.textViewDashboardIntervalOfEvents
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        val addEventFab = binding.dashbordFabAddEvent
        addEventFab.setOnClickListener {
            Toast.makeText(context, "Добавить новое событие", Toast.LENGTH_SHORT).show()
        }

        // TODO: REMOVE TEST HACK
        clearWidgetDB()
        eventsList.forEach { addToWidget(event) }
        addToWidget(EventData(EventType.SIMPLE,
            null,
            null,
            LocalDate.of(2023,5,31),
            LocalTime.now(), LocalTime.now(),"Дедлайн по eventreminder"))

    }

    fun clearWidgetDB() =
        requireActivity()
            .applicationContext
            .contentResolver
            .delete(Contract.PATH_EVENTS_URI,null,null)

     @RequiresApi(Build.VERSION_CODES.O)
     fun addToWidget(eventData: EventData){
        val values = ContentValues()
         values.put(Contract.COL_EVENT_TITLE, eventData.name)
         values.put(Contract.COL_EVENT_TYPE, eventData.type.toString())
         if (eventData.type != EventType.BIRTHDAY)
            values.put(Contract.COL_EVENT_DATE, eventData.date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
         else
             values.put(Contract.COL_EVENT_DATE, eventData.birthday?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
         values.put(Contract.COL_EVENT_TIME, eventData.time.format(DateTimeFormatter.ofPattern("HH:mm")))

         val uri: Uri? = requireActivity()
            .applicationContext
            .contentResolver
            .insert(Contract.PATH_EVENTS_URI, values)

        if (uri != null) {
            requireActivity().runOnUiThread {
               AppWidget.sendRefreshBroadcast(requireActivity() as MainActivity)
            }
        } else {
            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireActivity(),
                    "Something went wrong, event cannot be created.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    override fun onDestroy() {
        dashboardAdapter = null
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}