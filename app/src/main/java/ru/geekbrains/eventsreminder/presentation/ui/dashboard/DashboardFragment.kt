package ru.geekbrains.eventsreminder.presentation.ui.dashboard


import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.android.support.DaggerFragment
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.databinding.FragmentDashboardBinding
import ru.geekbrains.eventsreminder.di.ViewModelFactory
import ru.geekbrains.eventsreminder.domain.*
import ru.geekbrains.eventsreminder.presentation.MainActivity
import ru.geekbrains.eventsreminder.presentation.ui.RusIntPlural
import ru.geekbrains.eventsreminder.presentation.ui.toAgeInWordsByDate
import ru.geekbrains.eventsreminder.presentation.ui.toInt
import ru.geekbrains.eventsreminder.widget.AppWidget
import ru.geekbrains.eventsreminder.repo.cache.Contract
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DashboardFragment : DaggerFragment() {

    private val binding: FragmentDashboardBinding by viewBinding()
    private var dashboardAdapter: DashboardRecyclerViewAdapter? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val dashboardViewModel by viewModels<DashboardViewModel>({ this }) { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        with((requireActivity() as MainActivity)) {
            if (!checkPermission()) initReminderRights()
        }
        dashboardViewModel.loadEvents()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                        val data = appState.data as List<EventData>

                        showEvents(data)
                        updateWidget(data)
                    }
                    is AppState.LoadingState -> {
                        //TODO:Show some animation
                    }
                    is AppState.ErrorState -> {
                        Toast.makeText(context, appState.error.toString(), Toast.LENGTH_LONG).show()
                        Log.d(TAG, appState.error.toString())
                    }

                }
            } catch (t: Throwable) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show()
                Log.d(TAG, t.toString())
            }
        }

    }


    fun showEvents(events: List<EventData>) {
        try {
            val diffResult = DiffUtil.calculateDiff(
                EventsDiffUtil(
                    dashboardViewModel.storedFilteredEvents,
                    events
                )
            )
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

    fun updateWidget(eventsList: List<EventData>) {
            requireActivity().runOnUiThread {
                AppWidget.sendRefreshBroadcast(requireActivity() as MainActivity)
            }
        }
//        clearWidgetDB()
//        eventsList.forEach { addToWidget(it) }
        // TODO: Finish project intime!
//        addToWidget(
//            EventData(
//                EventType.SIMPLE,
//                null,
//                null,
//                LocalDate.of(2023, 5, 31),
//                LocalTime.now(), LocalTime.now(), "Дедлайн по eventreminder"
//            )
//        )
//    }

    fun clearWidgetDB() =
        requireActivity()
            .applicationContext
            .contentResolver
            .delete(Contract.PATH_EVENTS_URI, null, null)

    fun addToWidget(eventData: EventData) {
        val values = ContentValues()
        values.put(Contract.COL_EVENT_TYPE, eventData.type.toString())
        values.put(Contract.COL_EVENT_PERIOD, eventData.period?.toString())
        values.put(Contract.COL_BIRTHDAY,eventData.birthday?.toInt())
        values.put(Contract.COL_EVENT_DATE, eventData.date.toInt())
        values.put(Contract.COL_EVENT_TIME, eventData.time.toInt())
        values.put(Contract.COL_TIME_NOTIFICATION, eventData.timeNotifications.toInt())
        values.put(Contract.COL_EVENT_TITLE, eventData.name)

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

    companion object{
        val TAG = "ru.geekbrains.eventsreminder.presentation.ui.dashboard.DashboardFragment"
    }

    override fun onDestroy() {
        dashboardAdapter = null
        super.onDestroy()
    }

}