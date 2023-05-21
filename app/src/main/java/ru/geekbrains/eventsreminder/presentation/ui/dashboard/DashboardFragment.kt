package ru.geekbrains.eventsreminder.presentation.ui.dashboard


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
import ru.geekbrains.eventsreminder.service.NotificationService
import ru.geekbrains.eventsreminder.usecases.EVENTS_DATA
import ru.geekbrains.eventsreminder.usecases.MINUTES_FOR_START_NOTIFICATION
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
            Toast.makeText(context,getString(R.string.toast_msg_events_list_renewed),Toast.LENGTH_SHORT).show()
        }
        dashboardAdapter = DashboardRecyclerViewAdapter(dashboardViewModel.storedFilteredEvents)
        binding.recyclerViewListOfEvents.adapter = dashboardAdapter
        binding.recyclerViewListOfEvents.isSaveEnabled = true
        dashboardAdapter!!.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        val addEventFab = binding.dashboardFabAddEvent
        addEventFab.setOnClickListener {
            findNavController().navigate(R.id.chooseNewEventTypeDialog)
        }
        dashboardViewModel.statesLiveData.observe(this.viewLifecycleOwner) { appState ->
            try {
                when (appState) {
                    is AppState.SuccessState<*> -> {
                        val data = appState.data as List<EventData>
                        if (binding.shimmerLayout.isShimmerVisible){
                            binding.shimmerLayout.hideShimmer()
                            binding.shimmerLayout.visibility = GONE
                            binding.recyclerViewListOfEvents.visibility = VISIBLE
                        }
                        showEvents(data)
                        updateWidget(data)
                        updateNotificationService(data)
                    }
                    is AppState.LoadingState -> {
                        //shimmer animation is on fragment load
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
    override fun onResume() {
        super.onResume()
        binding.shimmerLayout.startShimmer()
    }
    override fun onPause() {
        super.onPause()
        binding.shimmerLayout.stopShimmer()
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
            binding.textViewDashboardHeader.text = buildString {
        append("всего ")
        append(
            RusIntPlural(
            "событ",
            events.count(),
            "ие", "ия", "ий"
        )
        )
        append(" за ")
        append(
            RusIntPlural(
            "д",
            dashboardViewModel.getDaysToShowEventsCount(),
            "ень", "ня", "ней"
        )
        )
    }
        } catch (t: Throwable) {
            dashboardViewModel.handleError(t)
        }
    }
    fun updateWidget(eventsList: List<EventData>) {
            requireActivity().runOnUiThread {
                AppWidget.sendRefreshBroadcast(requireActivity() as MainActivity)
            }
        }
    fun updateNotificationService(eventsList: List<EventData>) {
        getActivity()?.startService(Intent(context, NotificationService::class.java).apply {
            putExtra(MINUTES_FOR_START_NOTIFICATION, dashboardViewModel.getMinutesForStartNotification())
            putParcelableArrayListExtra(EVENTS_DATA, ArrayList(eventsList))}
        )
    }
    companion object{
        val TAG = "ru.geekbrains.eventsreminder.presentation.ui.dashboard.DashboardFragment"
    }
    override fun onDestroy() {
        dashboardAdapter = null
        super.onDestroy()
    }
}