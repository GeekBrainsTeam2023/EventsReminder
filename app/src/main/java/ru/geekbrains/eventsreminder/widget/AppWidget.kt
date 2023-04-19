package ru.geekbrains.eventsreminder.widget


import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.presentation.MainActivity


class AppWidget : AppWidgetProvider() {

    val EXTRA_LABEL = "EVENTS_TEXT"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
                        val intentActivity =Intent(context, MainActivity::class.java)
            //Intent("android.intent.action.MAIN")
            //intentActivity.addCategory("android.intent.category.LAUNCHER")
            //intentActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            //intentActivity.component = ComponentName(context, MainActivity::class.java)
            val pendIntent = PendingIntent.getActivity(context,appWidgetId,intentActivity,PendingIntent.FLAG_IMMUTABLE)

            val widgetView = RemoteViews(
                context.packageName,
                R.layout.app_widget
            )

            val intent = Intent(context, MyWidgetRemoteViewsService::class.java)

            widgetView.setRemoteAdapter(R.id.widgetList, intent)

            // template to handle the click listener for each item
            val clickIntentTemplate = Intent(context, MainActivity::class.java)
            val clickPendingIntentTemplate: PendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(appWidgetId, PendingIntent.FLAG_UPDATE_CURRENT xor PendingIntent.FLAG_IMMUTABLE)

            widgetView.setPendingIntentTemplate(R.id.widgetList,clickPendingIntentTemplate)

            appWidgetManager.updateAppWidget(appWidgetId, widgetView)
        }
    }

    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        if (action == AppWidgetManager.ACTION_APPWIDGET_UPDATE ||
            action == AppWidgetManager.ACTION_APPWIDGET_BIND
        ) {
            // refresh all your widgets
            val mgr = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context!!, AppWidget::class.java)
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widgetList)
        }
        super.onReceive(context, intent)
    }

    companion object {
        fun sendRefreshBroadcast(activity: MainActivity) {
            val ids: IntArray = AppWidgetManager.getInstance(activity.application)
                .getAppWidgetIds(ComponentName(activity.application, AppWidget::class.java))
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            intent.component = ComponentName(activity, AppWidget::class.java)
            activity.sendBroadcast(intent)
        }
    }
}