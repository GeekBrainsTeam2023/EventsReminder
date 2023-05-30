package ru.geekbrains.eventsreminder.presentation.ui.myevents

import android.app.AlertDialog
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
import ru.geekbrains.eventsreminder.databinding.FragmentMyEventsBinding
import ru.geekbrains.eventsreminder.di.ViewModelFactory
import ru.geekbrains.eventsreminder.domain.AppState
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.presentation.ui.EVENT_ID
import ru.geekbrains.eventsreminder.presentation.ui.RusIntPlural
import ru.geekbrains.eventsreminder.presentation.ui.callAfterRedrawViewTree
import ru.geekbrains.eventsreminder.presentation.ui.dashboard.EventsDiffUtil
import javax.inject.Inject


class MyEventsFragment : DaggerFragment() {
    private val binding: FragmentMyEventsBinding by viewBinding()
    private var myEventsAdapter: MyEventsRecyclerViewAdapter? = null
    private val layoutManager = CenterLayoutManager(context)
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val myEventsViewModel by viewModels<MyEventsViewModel>({ this }) { viewModelFactory }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_my_events, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            myEventsViewModel.loadMyEvents()
        } catch (t: Throwable) {
            myEventsViewModel.handleError(t)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            myEventsViewModel.loadMyEvents()
        } catch (t: Throwable) {
            myEventsViewModel.handleError(t)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.applyMarkupOptions()
            myEventsViewModel.statesLiveData.observe(this.viewLifecycleOwner) { appState ->
                processAppState(appState)
            }
        } catch (t: Throwable) {
            myEventsViewModel.handleError(t)
        }
    }

    private fun FragmentMyEventsBinding.applyMarkupOptions() {
        try {
            if (myEventsViewModel.cachedLocalEvents.isNotEmpty()) {
                showButtonAndHeader()
            } else {
                hideButtonAndHeader()
            }
            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                myEventsViewModel.loadMyEvents()
                Toast.makeText(
                    context,
                    getString(R.string.toast_msg_events_list_renewed),
                    Toast.LENGTH_SHORT
                ).show()
            }
            clearAllLocalEventsBtn.setOnClickListener { confirmDeletionOfAllEventsDialog() }
            myEventsAdapter =
                MyEventsRecyclerViewAdapter(myEventsViewModel.storedEvents, myEventsViewModel)
            RvListOfMyEvents.adapter = myEventsAdapter
            RvListOfMyEvents.isSaveEnabled = true
            RvListOfMyEvents.setLayoutManager(layoutManager)
            myEventsAdapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        } catch (t: Throwable) {
            myEventsViewModel.handleError(t)
        }
    }

    private fun processAppState(appState: AppState) {
        try {
            when (appState) {
                is AppState.SuccessState<*> -> {
                    val data = appState.data as List<EventData>
                    showEvents(data)
                }

                is AppState.LoadingState -> {

                }

                is AppState.ErrorState -> {
                    logAndToast(appState.error)
                }
            }
        } catch (t: Throwable) {
            myEventsViewModel.handleError(t)
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

    private fun hideButtonAndHeader() {
        try {
            binding.myEventsAreEmptyTextviewText.visibility = View.VISIBLE
            binding.textViewMyEventsHeader.visibility = View.GONE
            binding.clearAllLocalEventsBtn.visibility = View.GONE
        } catch (t: Throwable) {
            myEventsViewModel.handleError(t)
        }
    }

    private fun showButtonAndHeader() {
        try {
            binding.myEventsAreEmptyTextviewText.visibility = View.GONE
            binding.textViewMyEventsHeader.visibility = View.VISIBLE
            binding.clearAllLocalEventsBtn.visibility = View.VISIBLE
        } catch (t: Throwable) {
            myEventsViewModel.handleError(t)
        }
    }

    private fun showEvents(events: List<EventData>) {
        try {
            if (events.any()) showButtonAndHeader()
            else hideButtonAndHeader()
            val diffResult = DiffUtil.calculateDiff(
                EventsDiffUtil(
                    myEventsViewModel.storedEvents,
                    events
                )
            )
            myEventsViewModel.storedEvents.clear()
            myEventsViewModel.storedEvents.addAll(events)
            myEventsAdapter?.let { diffResult.dispatchUpdatesTo(it) }
            binding.textViewMyEventsHeader.text = buildString {
                append("всего ")
                append(
                    RusIntPlural(
                        "событ",
                        events.count(),
                        "ие", "ия", "ий"
                    )
                )
            }

            arguments?.getLong(EVENT_ID)?.let {
                events.indexOfFirst{event-> event.sourceId == it}.let {pos->
                if (pos >= 0 && pos < events.count())
                    callAfterRedrawViewTree {
                        with(binding.RvListOfMyEvents) {
                            smoothScrollToPosition(pos)
                            myEventsAdapter?.let{
                                it.notifyItemChanged(pos)
                                it.selectedPos = pos
                                it.notifyItemChanged(pos)
                           }
                        }
                    }
                }
            }
            arguments?.clear()
        } catch (t: Throwable) {
            myEventsViewModel.handleError(t)
        }
    }

    private fun confirmDeletionOfAllEventsDialog() {
        try {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.delete_all_local_events_dialog_title))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.delete_local_events_dialog_positive_btn)) { dialog, id ->
                    try {
                        myEventsViewModel.clearAllLocalEvents()
                        hideButtonAndHeader()
                    } catch (t: Throwable) {
                        myEventsViewModel.handleError(t)
                    }
                }
                .setNegativeButton(getString(R.string.delete_local_events_dialog_negative_btn)) { _, _ -> }
            val dlg = builder.create()
            dlg.show()
        } catch (t: Throwable) {
            myEventsViewModel.handleError(t)
        }
    }

    override fun onDestroy() {
        myEventsAdapter = null
        super.onDestroy()
    }
}