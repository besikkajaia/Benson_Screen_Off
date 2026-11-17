package com.benson.screenoff

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/** Виджет на рабочем столе: нажатие сразу отправляет команду блокировки, без запуска приложения. */
class LockWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val intent = Intent(context, LockReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val views = RemoteViews(context.packageName, R.layout.widget)
        views.setOnClickPendingIntent(R.id.widget_button, pending)
        for (id in appWidgetIds) {
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
