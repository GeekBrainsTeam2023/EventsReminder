package ru.geekbrains.eventsreminder.presentation.ui.dashboard


import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.databinding.FragmentDashboardBinding
import ru.geekbrains.eventsreminder.domain.*
import ru.geekbrains.eventsreminder.presentation.MainActivity
import ru.geekbrains.eventsreminder.widget.AppWidget
import ru.geekbrains.eventsreminder.widget.Contract
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
class DashboardFragment : Fragment() {

    private val binding: FragmentDashboardBinding by viewBinding()
    private var dashboardAdapter: DashboardRecyclerViewAdapter? = null
    private lateinit var dashboardViewModel : DashboardViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        with((requireActivity() as MainActivity)){
            if (!checkPermission()) initReminderRights()
        }
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //testDataShowing()
        dashboardAdapter = DashboardRecyclerViewAdapter(dashboardViewModel.storedFilteredEvents)
        binding.recyclerViewListOfEvents.adapter = dashboardAdapter
        binding.recyclerViewListOfEvents.isSaveEnabled = true
        dashboardAdapter!!.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        val addEventFab = binding.dashboardFabAddEvent
        addEventFab.setOnClickListener {
            Toast.makeText(context, "Добавить новое событие", Toast.LENGTH_SHORT).show()
        }

        dashboardViewModel.statesLiveData.observe(this.viewLifecycleOwner) { appState ->
            try {
                when (appState) {
                   is AppState.SuccessState<*> -> {
                       val data = appState.data as  List<EventData>

                       showEvents(data)
                       updateWidget(data)
                       }
                   is AppState.LoadingState-> {
                   //TODO:Show some animation
                        }
                   is AppState.ErrorState -> dashboardViewModel.handleError(appState.error)

                }
            }catch (t:Throwable){dashboardViewModel.handleError(t)}
        }
        dashboardViewModel.loadEvents()
    }


    fun showEvents(events: List<EventData>) {
        try {
            val diffResult = DiffUtil.calculateDiff(EventsDiffUtil(dashboardViewModel.storedFilteredEvents, events))
            dashboardViewModel.storedFilteredEvents.clear()
            dashboardViewModel.storedFilteredEvents.addAll(events)
            dashboardAdapter?.let { diffResult.dispatchUpdatesTo(it) }
            binding.textViewDashboardHeader.text = "всего " + RusIntPlural(
                "событ",
                events.count(),
                "ие", "ия", "ий"
            ) + " за " + RusIntPlural(
                "д",
                dashboardViewModel.getDatysToShowEventsCount(),
                "ень", "ня", "ней"
            )
        } catch (t: Throwable) {
            dashboardViewModel.handleError(t)
        }
    }

    fun updateWidget(eventsList: List<EventData>){
        clearWidgetDB()
        eventsList.forEach { addToWidget(it) }
        // TODO: Finish project intime!
        addToWidget(EventData(EventType.SIMPLE,
            null,
            null,
            LocalDate.of(2023,5,31),
            LocalTime.now(), LocalTime.now(),"Дедлайн по eventreminder"))

    }


    fun testDataShowing(){

            // val eventItems: List<DashboardRecyclerviewItem>? = mapOfEvents?.let {  }
            val event = EventData(
                EventType.BIRTHDAY,
                PeriodType.DAY,
                LocalDate.of(2000, 3, 22),
                LocalDate.of(2000, 3, 22),
                LocalTime.of(18, 30, 22),
                LocalTime.of(10, 20, 55),
                "Vasiliy Ivanovich Zagorulko"
            )
            val event1 = EventData(
                EventType.HOLIDAY,
                PeriodType.DAY,
                null,
                LocalDate.of(0, 3, 8),
                LocalTime.of(0, 0, 0),
                LocalTime.of(10, 20, 55),
                "Holiday of Holidays"
            )
            val event2 = EventData(
                EventType.BIRTHDAY,
                PeriodType.DAY,
                LocalDate.of(2003, 11, 6),
                LocalDate.of(2003, 11, 6),
                LocalTime.of(0, 0, 0),
                LocalTime.of(11, 10, 0),
                "Svetlana"
            )
            val event3 = EventData(
                EventType.SIMPLE,
                PeriodType.MONTH,
                null,
                LocalDate.of(2023, 11, 27),
                LocalTime.of(16, 30, 0),
                LocalTime.of(15, 30, 0),
                "Visit to my dantist"
            )
            val eventsList: List<EventData> = listOf(event, event1, event1, event2, event3)
            dashboardAdapter = DashboardRecyclerViewAdapter(eventsList)
     }

    fun clearWidgetDB() =
        requireActivity()
            .applicationContext
            .contentResolver
            .delete(Contract.PATH_EVENTS_URI,null,null)

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