package ru.geekbrains.eventsreminder.presentation.ui.dashboard


import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private val binding: FragmentDashboardBinding by viewBinding()
    private var dashboardAdapter: DashboardRecyclerViewAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val event = Event(
            1,
            "Birthday",
            "Vasiliy Ivanovich Zagorulko",
            "ic_notification_add_24",
            "22.03.2000",
            "3 days"
        )
        val event1 = Event(2, "Holiday", "Holiday", "ic_notification_add_24.xml", "08.03", "3 days")
        val event2 = Event(3, "Birthday", "Svetlana", "ic_notification_add_24", "11.06.2003", "10 days")
        val event3 =
            Event(4, "SimpleEvent", "Visit to my dantist", "ic_notification_add_24.xml", "27.01.2020", "today")
        val eventsList: List<Event> = listOf(event, event1, event2, event3)
        dashboardAdapter = DashboardRecyclerViewAdapter(eventsList)
        binding.recyclerViewListOfEvents.adapter = dashboardAdapter
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        val textView: TextView =  binding.textViewDashboardIntervalOfEvents
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        val addEventFab = binding.dashbordFabAddEvent
        addEventFab.setOnClickListener {
            Toast.makeText(context, "Добавить новое событие", Toast.LENGTH_SHORT).show()
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