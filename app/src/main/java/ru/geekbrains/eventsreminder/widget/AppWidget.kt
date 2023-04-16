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


class AppWidget : AppWidgetProvider() {

    val EXTRA_LABEL = "EVENTS_TEXT"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(
                context.packageName,
                R.layout.app_widget
            )

            // click event handler for the title, launches the app when the user clicks on title
//
//            val titleIntent = Intent(context, MainActivity::class.java)
//            val titlePendingIntent = PendingIntent.getActivity(context, 0, titleIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT xor PendingIntent.FLAG_IMMUTABLE)
//            views.setOnClickPendingIntent(R.id.itemAppWidget, titlePendingIntent)
            val intent = Intent(context, MyWidgetRemoteViewsService::class.java)
            views.setRemoteAdapter(R.id.widgetList, intent)
//            val intentActivity = Intent(context,MainActivity::class.java)
//            val pendIntent = PendingIntent.getActivity(context,0,intentActivity,PendingIntent.FLAG_IMMUTABLE)
//            views.setOnClickPendingIntent(R.id.itemAppWidget,pendIntent)
            // template to handle the click listener for each item
//            val clickIntentTemplate = Intent(context, MainActivity::class.java)
//            val clickPendingIntentTemplate: PendingIntent = TaskStackBuilder.create(context)
//                .addNextIntentWithParentStack(clickIntentTemplate)
//                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT xor PendingIntent.FLAG_IMMUTABLE)
//            views.setPendingIntentTemplate(R.id.itemAppWidget, clickPendingIntentTemplate)
            appWidgetManager.updateAppWidget(appWidgetId, views)
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
        fun sendRefreshBroadcast(context: Context) {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            intent.component = ComponentName(context, AppWidget::class.java)
            context.sendBroadcast(intent)
        }
    }
}