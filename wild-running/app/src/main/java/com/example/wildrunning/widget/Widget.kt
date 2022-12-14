package com.example.wildrunning.widget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.wildrunning.R
import com.example.wildrunning.activity.LoginActivity
import com.example.wildrunning.activity.MainActivity
import com.example.wildrunning.activity.MainActivity.Companion.widgetDistance
import com.example.wildrunning.activity.MainActivity.Companion.widgetTime

class Widget: AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        if (context != null && appWidgetManager != null && appWidgetIds != null) {

            appWidgetIds.forEach {
                val views = RemoteViews(context.packageName, R.layout.widget)
                views.setTextViewText(R.id.tvWidgetChrono, widgetTime)
                views.setTextViewText(R.id.tvWidgetDistance, widgetDistance)

                val loginIntent = Intent(context, LoginActivity::class.java).let { intent ->
                    PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)
                }

                views.setOnClickPendingIntent(R.id.ivUser, loginIntent)

                appWidgetManager.updateAppWidget(it, views)
            }
        }


    }
}