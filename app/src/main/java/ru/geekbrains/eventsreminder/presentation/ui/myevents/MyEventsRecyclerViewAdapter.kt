package ru.geekbrains.eventsreminder.presentation.ui.myevents

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.domain.EventData
import ru.geekbrains.eventsreminder.presentation.MainActivity
import ru.geekbrains.eventsreminder.presentation.ui.findActivity


class MyEventsRecyclerViewAdapter(
    private var myEvents: List<EventData>,
    private val viewModel: MyEventsViewModel,
	private val context: Context
) : ItemTouchHelperAdapter, RecyclerView.Adapter<MyEventsViewHolder>() {
	var selectedPos = RecyclerView.NO_POSITION

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEventsViewHolder =
		MyEventsViewHolder(
			LayoutInflater.from(parent.context)
				.inflate(R.layout.my_events_recyclerview_item, parent, false)
		)

	override fun getItemCount(): Int = myEvents.size

	override fun onBindViewHolder(holder: MyEventsViewHolder, position: Int) {
		try {
			holder.bind(
				myEvents[position],
				position == 0 || myEvents[position - 1].date != myEvents[position].date,
				selectedPos == position,
				viewModel
			)

		} catch (t: Throwable) {
			Log.e(this::class.java.toString(), "", t)
		}
	}

	override fun onItemDismiss(myEventsViewHolder: MyEventsViewHolder) {
		confirmDeletionOfLocalEventDialog(viewModel,myEvents[myEventsViewHolder.layoutPosition])
	}

	private fun confirmDeletionOfLocalEventDialog(viewModel: MyEventsViewModel,item:EventData) {
		try {
			val activity = context.findActivity() as MainActivity
			val builder = AlertDialog.Builder(activity)
			builder.setTitle(activity.getString(R.string.delete_local_event_dialog_title) + " " + "\"${item.name}\"" + "?")
				.setCancelable(true)
				.setPositiveButton(activity.getString(R.string.delete_local_events_dialog_positive_btn)) { _, _ ->
					try {
						viewModel.deleteMyEvent(item)
						Toast.makeText(
							activity.applicationContext,
							activity.getString(R.string.toast_delete_local_event_dialod),
							Toast.LENGTH_SHORT
						).show()

					} catch (t: Throwable) {
						Log.e(this::class.java.toString(), "", t)
					}
				}
				.setNegativeButton(activity.getString(R.string.delete_local_events_dialog_negative_btn)) { _, _ ->
					notifyDataSetChanged()
				}
			val dlg = builder.create()
			dlg.show()
		} catch (t: Throwable) {
			Log.e(this::class.java.toString(), "", t)
		}
	}

}