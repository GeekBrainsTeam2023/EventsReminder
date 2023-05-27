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
        try {
            with((requireActivity() as MainActivity)) {
                if (!checkPermission()) initReminderRights()
            }
            dashboardViewModel.loadEvents()
        } catch (t: Throwable) {
            dashboardViewModel.handleError(t)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.swipeLayout.setOnRefreshListener {
                try {
                    binding.swipeLayout.isRefreshing = false
                    dashboardViewModel.loadEvents()
                    Toast.makeText(
                        context,
                        getString(R.string.toast_msg_events_list_renewed),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (t: Throwable) {
                    dashboardViewModel.handleError(t)
                }
            }
            dashboardAdapter = DashboardRecyclerViewAdapter(dashboardViewModel.storedFilteredEvents)
            binding.recyclerViewListOfEvents.adapter = dashboardAdapter
            binding.recyclerViewListOfEvents.isSaveEnabled = true
            dashboardAdapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            val addEventFab = binding.dashboardFabAddEvent
            addEventFab.setOnClickListener {
                try {
                    findNavController().navigate(R.id.chooseNewEventTypeDialog)
                } catch (t: Throwable) {
                    dashboardViewModel.handleError(t)
                }
            }
            dashboardViewModel.statesLiveData.observe(this.viewLifecycleOwner) { appState ->
                try {
                    when (appState) {
                        is AppState.SuccessState<*> -> {
                            val data = appState.data as List<EventData>
                            if (binding.shimmerLayout.isShimmerVisible) {
                                binding.shimmerLayout.hideShimmer()
                                binding.shimmerLayout.visibility = GONE
                                binding.recyclerViewListOfEvents.visibility = VISIBLE
                            }
                            showEvents(data)
                            updateWidget()
                            updateNotificationService(data)
                        }

                        is AppState.LoadingState -> {
                            //shimmer animation is on fragment load
                        }

                        is AppState.ErrorState -> {
                            logAndToast(appState.error)
                        }
                    }
                } catch (t: Throwable) {
                    logAndToast(t)
                }
            }
        } catch (t: Throwable) {
            dashboardViewModel.handleError(t)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            binding.shimmerLayout.startShimmer()
            dashboardViewModel.loadEvents()
        } catch (t: Throwable) {
            dashboardViewModel.handleError(t)
        }
    }

    override fun onPause() {
        super.onPause()
        try{
            binding.shimmerLayout.stopShimmer()
        } catch (t: Throwable) {
            dashboardViewModel.handleError(t)
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

    fun updateWidget() {
        try {
            requireActivity().runOnUiThread {
                AppWidget.sendRefreshBroadcast(requireActivity() as MainActivity)
            }
        } catch (t: Throwable) {
            dashboardViewModel.handleError(t)
        }
    }

    fun updateNotificationService(eventsList: List<EventData>) {
        try {
            activity?.startService(Intent(context, NotificationService::class.java).apply {
                putExtra(
                    MINUTES_FOR_START_NOTIFICATION,
                    dashboardViewModel.getMinutesForStartNotification()
                )
                putParcelableArrayListExtra(EVENTS_DATA, ArrayList(eventsList))
            }
            )
        } catch (t: Throwable) {
            dashboardViewModel.handleError(t)
        }
    }

    private fun logAndToast(t:Throwable) = logAndToast(t,this::class.java.toString())

    private fun logAndToast(t: Throwable, tag:String?) {
        try {
            Log.e(tag, "", t)
            Toast.makeText(requireContext().applicationContext, t.toString(), Toast.LENGTH_LONG).show()
        } catch (_: Throwable) {
        }
    }

    override fun onDestroy() {
        dashboardAdapter = null
        super.onDestroy()
    }
}