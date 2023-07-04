package ru.geekbrains.eventsreminder.widget


import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import ru.geekbrains.eventsreminder.R
import ru.geekbrains.eventsreminder.presentation.MainActivity
import kotlin.math.min


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

			// Сюда вставить из настроек
			val backgroundColor = context.resources.getColor(R.color.white, context.theme)
			val strokeColor = context.resources.getColor(R.color.color_primary, context.theme)
			val strokeWidth = 3
			val cornerRadius = 16f

			val widgetView = RemoteViews(
				context.packageName,
				R.layout.app_widget
			)
			val intent = Intent(context, MyWidgetRemoteViewsService::class.java)
			widgetView.setImageViewBitmap(
				R.id.background,
				getWidgetBitmap(
					context,
					getWidgetSize(context, appWidgetId),
					backgroundColor,
					strokeColor,
					strokeWidth,
					cornerRadius
				)
			)
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

	private fun getWidgetSize(context: Context, widgetId: Int): Pair<Int, Int> {
		val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
		val width = getWidgetWidth(context, isPortrait, widgetId)
		val height = getWidgetHeight(context, isPortrait, widgetId)
		val widthInPx = context.dip(width)
		val heightInPx = context.dip(height)
		return Pair(widthInPx, heightInPx)
	}

	private fun getWidgetWidth(context: Context, isPortrait: Boolean, widgetId: Int): Int =
		if (isPortrait) {
			getWidgetSizeInDp(context, widgetId, AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
		} else {
			getWidgetSizeInDp(context, widgetId, AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
		}

	private fun getWidgetHeight(context: Context, isPortrait: Boolean, widgetId: Int): Int =
		if (isPortrait) {
			getWidgetSizeInDp(context, widgetId, AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
		} else {
			getWidgetSizeInDp(context, widgetId, AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
		}

	private fun getWidgetSizeInDp(context: Context, widgetId: Int, key: String): Int =
		AppWidgetManager.getInstance(context).getAppWidgetOptions(widgetId).getInt(key, 0)

	private fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
	private fun getWidgetBitmap(
		context: Context,
		size: Pair<Int, Int>,
		@ColorInt backgroundColor: Int,
		@ColorInt strokeColor: Int,
		strokeWidth: Int,
		cornerRadius: Float
	): Bitmap {
		val paint = Paint(Paint.FILTER_BITMAP_FLAG and Paint.DITHER_FLAG and Paint.ANTI_ALIAS_FLAG).apply {
			setStrokeWidth(strokeWidth.toFloat())
			style = Paint.Style.STROKE
			strokeCap = Paint.Cap.ROUND
		}
		val rect = RectF().apply {
			set(0f, 0f, size.first.toFloat(), size.second.toFloat())
		}

		val bitmap = Bitmap.createBitmap(size.first, size.second, Bitmap.Config.ARGB_8888)
		val canvas = Canvas(bitmap)
		paint.apply {
			style = Paint.Style.FILL
			color = backgroundColor
		}

		canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
		val stroke = RectF().apply {
			set((strokeWidth / 2f), (strokeWidth / 2f), size.first - (strokeWidth / 2f), size.second - (strokeWidth / 2f))
		}
		paint.apply {
			style = Paint.Style.STROKE
			color = strokeColor
		}
		val strokeCornerRadius = cornerRadius - (strokeWidth / 2f)
		canvas.drawRoundRect(stroke, strokeCornerRadius, strokeCornerRadius, paint)

		return bitmap
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