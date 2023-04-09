package ru.geekbrains.eventsreminder.ui.notifications

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.databinding.FragmentHomeBinding
import ru.geekbrains.eventsreminder.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private val binding: FragmentNotificationsBinding by viewBinding()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_notifications, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        val menuHost : MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.removeItem(R.id.notifications)
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = false

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}