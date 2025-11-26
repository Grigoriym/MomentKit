package com.grappim.momentkit.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.grappim.momentkit.R
import com.grappim.momentkit.RString
import com.grappim.momentkit.formatters.DateTimeFormatters
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class MomentKitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        scheduleNextUpdate(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: android.os.Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        scheduleNextUpdate(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelScheduledUpdate(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_UPDATE_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, MomentKitWidgetProvider::class.java)
            )
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val currentDateTime = LocalDateTime.now()

        // Get widget dimensions to choose appropriate layout
        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
        val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

        // Choose layout based on size
        val layoutId = getLayoutForSize(minWidth, minHeight)

        val views = RemoteViews(context.packageName, layoutId).apply {
            // Set up click intent to open MainActivity
            val intent = Intent(context, com.grappim.momentkit.MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            // Always update time and date
            setTextViewText(
                R.id.widget_time_24h,
                DateTimeFormatters.format24HourNoSeconds(currentDateTime)
            )
            setTextViewText(
                R.id.widget_date_ddmmyyyy,
                DateTimeFormatters.formatDDMMYYYY(currentDateTime)
            )

            // Update title and additional fields if layout has them (medium/large only)
            if (layoutId != R.layout.widget_moment_kit_small) {
                setTextViewText(R.id.widget_title, context.getString(RString.widget_title))
                setTextViewText(
                    R.id.widget_time_12h,
                    DateTimeFormatters.format12HourNoSeconds(currentDateTime)
                )
                setTextViewText(
                    R.id.widget_date_mmddyyyy,
                    DateTimeFormatters.formatMMDDYYYY(currentDateTime)
                )
                setTextViewText(
                    R.id.widget_date_yyyymmdd,
                    DateTimeFormatters.formatYYYYMMDD(currentDateTime)
                )
            }

            // Update extra info for large layout only
            if (layoutId == R.layout.widget_moment_kit_large) {
                setTextViewText(
                    R.id.widget_week_of_year,
                    "Week: ${DateTimeFormatters.getWeekOfYear(currentDateTime)}"
                )
                setTextViewText(
                    R.id.widget_day_of_week,
                    DateTimeFormatters.getDayOfWeek(currentDateTime)
                )
                setTextViewText(
                    R.id.widget_month,
                    DateTimeFormatters.getMonth(currentDateTime)
                )
            }
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getLayoutForSize(width: Int, height: Int): Int = when {
        // Small: 2x2
        width < 150 || height < 150 -> R.layout.widget_moment_kit_small
        // Large: 4x3+
        width >= 250 && height >= 180 -> R.layout.widget_moment_kit_large
        // Medium: 3x3 (default)
        else -> R.layout.widget_moment_kit
    }

    private fun scheduleNextUpdate(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MomentKitWidgetProvider::class.java).apply {
            action = ACTION_UPDATE_WIDGET
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule update at the start of next minute using LocalDateTime
        val nextMinute = LocalDateTime.now()
            .plusMinutes(1)
            .truncatedTo(ChronoUnit.MINUTES)

        val nextUpdateMillis = nextMinute
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // Check if we can schedule exact alarms (Android 12+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC,
                    nextUpdateMillis,
                    pendingIntent
                )
            } else {
                // Fallback to inexact alarm if permission not granted
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC,
                    nextUpdateMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC,
                nextUpdateMillis,
                pendingIntent
            )
        }
    }

    private fun cancelScheduledUpdate(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MomentKitWidgetProvider::class.java).apply {
            action = ACTION_UPDATE_WIDGET
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        private const val ACTION_UPDATE_WIDGET = "com.grappim.momentkit.ACTION_UPDATE_WIDGET"
    }
}
