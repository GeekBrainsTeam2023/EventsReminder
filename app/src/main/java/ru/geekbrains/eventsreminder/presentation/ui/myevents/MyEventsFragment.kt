package ru.geekbrains.eventsreminder.presentation.ui.myevents

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
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
import ru.geekbrains.eventsreminder.presentation.ui.RusIntPlural
import ru.geekbrains.eventsreminder.presentation.ui.dashboard.EventsDiffUtil
import javax.inject.Inject


class MyEventsFragment : DaggerFragment() {
    private val binding: FragmentMyEventsBinding by viewBinding()
    private var myEventsAdapter: MyEventsRecyclerViewAdapter? = null
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val myEventsViewmodel by viewModels<MyEventsViewModel>({ this }) { viewModelFactory }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_my_events, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myEventsViewmodel.loadMyEvents()
    }

    override fun onResume() {
        super.onResume()
        myEventsViewmodel.loadMyEvents()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(myEventsViewmodel.cachedLocalEvents.isNotEmpty()){
            binding.myEventsAreEmptyTextviewText.visibility = View.GONE
        } else {
            hideButtonAndHeader()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            myEventsViewmodel.loadMyEvents()
            Toast.makeText(context,getString(R.string.toast_msg_events_list_renewed), Toast.LENGTH_SHORT).show()
        }
        binding.clearAllLocalEventsBtn.setOnClickListener { confirmDeletionOfAllEventsDialog() }
        myEventsAdapter = MyEventsRecyclerViewAdapter(myEventsViewmodel.storedEvents,myEventsViewmodel)
        binding.RvListOfMyEvents.adapter = myEventsAdapter
        binding.RvListOfMyEvents.isSaveEnabled = true
        myEventsAdapter!!.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        myEventsViewmodel.statesLiveData.observe(this.viewLifecycleOwner) { appState ->
            try {
                when (appState) {
                    is AppState.SuccessState<*> -> {
                        val data = appState.data as List<EventData>
                        showEvents(data)
                    }
                    is AppState.LoadingState -> {

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

    private fun hideButtonAndHeader() {
        binding.myEventsAreEmptyTextviewText.visibility = View.VISIBLE
        binding.textViewMyEventsHeader.visibility = View.GONE
        binding.clearAllLocalEventsBtn.visibility = View.GONE
    }

    fun showEvents(events: List<EventData>) {
        try {
            val diffResult = DiffUtil.calculateDiff(
                EventsDiffUtil(
                    myEventsViewmodel.storedEvents,
                    events
                )
            )
            myEventsViewmodel.storedEvents.clear()
            myEventsViewmodel.storedEvents.addAll(events)
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
        } catch (t: Throwable) {
            myEventsViewmodel.handleError(t)
        }
    }
    private fun confirmDeletionOfAllEventsDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.delete_all_local_events_dialog_title))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.delete_local_events_dialog_positive_btn)) { dialog, id ->
                myEventsViewmodel.clearAllLocalEvents()
                hideButtonAndHeader()
            }
            .setNegativeButton(getString(R.string.delete_local_events_dialog_negative_btn)) { _, _ ->}
        val dlg = builder.create()
        dlg.show()
    }
    companion object{
        val TAG = "ru.geekbrains.eventsreminder.presentation.ui.myevents.MyEventsFragment"
    }
    override fun onDestroy() {
        myEventsAdapter = null
        super.onDestroy()
    }
}