package com.jmulla.notificationnotes

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.os.Vibrator
import android.provider.Settings
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.content.WakefulBroadcastReceiver
import android.support.v7.app.NotificationCompat


/**
 * Created by Jamal on 07/07/2017.
 */

class ReminderNotificationReceiver : WakefulBroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val service1 = Intent(context, ReminderAlarmService::class.java)
        val id : String? = intent?.getStringExtra("reminderID")
        service1.putExtra("reminderID", id)
        context?.startService(service1)
    }


}

class ReminderAlarmService : Service() {
    internal var count = 100
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        count += 1
        val dbH : DatabaseHandler = DatabaseHandler(baseContext)
        val id : String = intent.getStringExtra("reminderID")
        val note : ReminderNote = dbH.getReminderNote(id)
        //Toast.makeText(applicationContext, dbH.getCount(DatabaseHandler.TABLE_REMINDERS).toString(), Toast.LENGTH_SHORT).show()
        val refreshIntent = Intent("refresh")
        refreshIntent.putExtra("id", id)
        LocalBroadcastManager.getInstance(baseContext).sendBroadcast(refreshIntent)
        dbH.deleteNote(id, DatabaseHandler.TABLE_REMINDERS)
        val mBuilder = NotificationCompat.Builder(baseContext)
        mBuilder.setContentTitle(note.Title)
        mBuilder.setContentText(note.Content)
        val v = baseContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(1000)
        mBuilder.setSmallIcon(R.drawable.ic_stat_notifications_none)
        //mBuilder.setVibrate(longArrayOf(0, 1000))
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
        mBuilder.setLights(Color.CYAN, 2000, 2000)
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val myNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mBuilder.setContentIntent(contentIntent)
        myNotificationManager.notify(count, mBuilder.build())
        return super.onStartCommand(intent, flags, startId)
    }
}

