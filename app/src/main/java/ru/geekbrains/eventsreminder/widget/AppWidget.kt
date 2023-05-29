package ru.geekbrains.eventsreminder.widget


import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.presentation.MainActivity


class AppWidget : AppWidgetProvider() {
	override fun onUpdate(
		context: Context,
		appWidgetManager: AppWidgetManager,
		appWidgetIds: IntArray
	) {
		try {
			for (appWidgetId in appWidgetIds) {
				onUpdateWidget(context, appWidgetId, appWidgetManager)
			}
		} catch (t: Throwable) {
			errLog(t)
		}
	}

	private fun onUpdateWidget(
		context: Context,
		appWidgetId: Int,
		appWidgetManager: AppWidgetManager
	) {
		try {
			val widgetView = RemoteViews(
				context.packageName,
				R.layout.app_widget
			)
			val intent = Intent(context, MyWidgetRemoteViewsService::class.java)
			widgetView.setRemoteAdapter(R.id.widgetList, intent)
			/**
			 * Темплейт pendingIntent с вызовом MainActivity для элементов списка.
			 * Обязательно в WigetRemoteViewsFactory.getViewAt нужно вызвать
			 * rv.setOnClickFillInIntent(R.id.itemAppWidget,Intent()), чтобы  клики по элементам
			 * списка работали.
			 */
			val clickIntentTemplate = Intent(context, MainActivity::class.java)
			val clickPendingIntentTemplate: PendingIntent = TaskStackBuilder.create(context)
				.addNextIntentWithParentStack(clickIntentTemplate)
				.getPendingIntent(
					0,
					PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
				)
			widgetView.setPendingIntentTemplate(R.id.widgetList, clickPendingIntentTemplate)
			// Тело списка сделаем кликабельным для возможности реакции на клики при пустом списке
			val intentActivity = Intent(context, MainActivity::class.java)
			val pendIntent = PendingIntent.getActivity(
				context,
				appWidgetId,
				intentActivity,
				PendingIntent.FLAG_IMMUTABLE
			)
			widgetView.setOnClickPendingIntent(R.id.widgetLayout, pendIntent)
			appWidgetManager.updateAppWidget(appWidgetId, widgetView)
		} catch (t: Throwable) {
			errLog(t)
		}
	}

	override fun onReceive(context: Context?, intent: Intent) {
		try {
			val action = intent.action
			if (action == AppWidgetManager.ACTION_APPWIDGET_UPDATE ||
				action == AppWidgetManager.ACTION_APPWIDGET_BIND
			) {
				// refresh all widgets
				val mgr = AppWidgetManager.getInstance(context)
				val cn = ComponentName(context!!, AppWidget::class.java)
				mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widgetList)
			}
			super.onReceive(context, intent)
		} catch (t: Throwable) {
			errLog(t)
		}
	}


	companion object {
		fun sendRefreshBroadcast(activity: MainActivity) {
			try {
				val ids: IntArray = AppWidgetManager.getInstance(activity.application)
					.getAppWidgetIds(ComponentName(activity.application, AppWidget::class.java))
				val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
				intent.component = ComponentName(activity, AppWidget::class.java)
				activity.sendBroadcast(intent)
			} catch (t: Throwable) {
				errLog(t)
			}
		}

		private fun errLog(t: Throwable) = errLog(t, this::class.java.toString())

		private fun errLog(t: Throwable, TAG: String) {
			try {
				Log.e(TAG, "", t)
			} catch (_: Throwable) {
			}
		}
	}
}