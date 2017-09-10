package com.jmulla.notificationnotes

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.Toast
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by Jamal on 05/08/2017.
 */
class ShowRevisionService : IntentService("noteDispatcher") {
    override fun onHandleIntent(intent: Intent?) {
/*        val alarmManager = baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(baseContext, IntervalNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(baseContext, 0, myIntent, 0)
        alarmManager.cancel(pendingIntent)
        val interval: Int?
        if(sp_random){
            interval = ThreadLocalRandom.current().nextInt(1, 59 + 1)
        }else{
            interval = interval_s
        }
        //Toast.makeText(baseContext, "Interval: $interval", Toast.LENGTH_SHORT).show()
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + (1000 * 60 * interval).toLong(),
                (1000 * 60 * interval).toLong(), pendingIntent)
        Toast.makeText(applicationContext, intent?.dataString, Toast.LENGTH_LONG).show()*/
    }
}