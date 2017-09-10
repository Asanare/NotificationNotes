package com.jmulla.notificationnotes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.preference.PreferenceManager

/**
 * Created by Jamal on 14/07/2017.
 */
class UpgradeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.MY_PACKAGE_REPLACED") {
            val dbH : DatabaseHandler = DatabaseHandler(context)
            val allReminders : MutableList<ReminderNote> = dbH.getAllReminders() as MutableList<ReminderNote>
            for (reminder in allReminders) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val myIntent = Intent(context, ReminderNotificationReceiver::class.java)
                myIntent.putExtra("reminderID", reminder.Id)
                val pendingIntent = PendingIntent.getBroadcast(context, reminder.Time.toInt(), myIntent, 0)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        reminder.Time, pendingIntent)

                val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
                val noteIntent = Intent(context, IntervalNotificationReceiver::class.java)
                val piNote = PendingIntent.getBroadcast(context, 0, noteIntent, 0)
                val interval: Int = sharedPrefs?.getString("etp_interval", "5")!!.toInt()
                val sp_enabled = sharedPrefs.getBoolean("sp_noti_enabled", true)
                if (sp_enabled) {
                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime() + (1000 * 60 * interval).toLong(),
                            (1000 * 60 * interval).toLong(), piNote)
                }
            }
        }
    }
}