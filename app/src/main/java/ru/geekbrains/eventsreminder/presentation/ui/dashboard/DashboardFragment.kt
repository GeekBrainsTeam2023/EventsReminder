package ru.geekbrains.eventsreminder.presentation.ui.dashboard


import android.content.Context

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
import ru.geekbrains.eventsreminder.widget.AppWidget
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
        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            dashboardViewModel.loadEvents()
            Toast.makeText(context,"Список событий обновлён",Toast.LENGTH_SHORT).show()
        }

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
                        Log.e(TAG, "",appState.error)
                    }

                }
            } catch (t: Throwable) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show()
                Log.e(TAG, "",t)
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


    companion object{
        val TAG = "ru.geekbrains.eventsreminder.presentation.ui.dashboard.DashboardFragment"
    }

    override fun onDestroy() {
        dashboardAdapter = null
        super.onDestroy()
    }

}