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
import android.support.v4.content.WakefulBroadcastReceiver
import android.support.v7.app.NotificationCompat
import java.util.*





/**
 * Created by Jamal on 21/06/2017.
 */

class IntervalNotificationReceiver : WakefulBroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val service1 = Intent(context, IntervalAlarmService::class.java)
        context?.startService(service1)
    }


}

/**
 * Created by Jamal on 21/06/2017.
 */
class IntervalAlarmService : Service() {
    internal var count = 0
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val dbH : DatabaseHandler = DatabaseHandler(baseContext)
        val intervalNotes: List<IntervalNote> = dbH.getAllIntervalNotes(DatabaseHandler.TABLE_INTERVAL) as List<IntervalNote>
        //val intervalNotes = ReadLines.readLines()
        if (intervalNotes.isNotEmpty()) {

            var note = intervalNotes[Random().nextInt(intervalNotes.size)]
            val allNotes = dbH.getCount(DatabaseHandler.TABLE_INTERVAL)
            var thisCount : Int = 1
            while(note.Enabled != 1 && (thisCount != allNotes)){
                note = intervalNotes[Random().nextInt(intervalNotes.size)]
                thisCount += 1
            }
            if(allNotes != thisCount || allNotes  == 1){
                val mBuilder = NotificationCompat.Builder(this)
                mBuilder.setContentTitle(note.Title)
                mBuilder.setContentText(note.Content)
                mBuilder.setSmallIcon(R.drawable.ic_stat_notifications_none)
                val v = baseContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                v.vibrate(1000)
                mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                mBuilder.setLights(Color.BLUE, 2000, 2000)
                val myNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationIntent = Intent(applicationContext, MainActivity::class.java)
                val contentIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                //val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
                //writeToFile(currentDateTimeString)
                mBuilder.setContentIntent(contentIntent)
                myNotificationManager.notify(count, mBuilder.build())
                stopSelf()
            }

        }
        return super.onStartCommand(intent, flags, startId)
    }

/*    fun writeToFile(data: String) {
        try {
            val path : String = Environment.getExternalStorageDirectory().path + File.separator  + "NOTIFICATION"
            val folder : File = File(path)
            folder.mkdirs()
            val file : File = File(folder, "log.txt")
            val stream = FileOutputStream(file, true)
            stream.use { _ ->
                stream.write(data.toByteArray() + ("\n").toByteArray())
            }
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }*/
}