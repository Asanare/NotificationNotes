package com.jmulla.notificationnotes

import android.app.AlarmManager
import android.app.Fragment
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ThreadLocalRandom


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var fragment : Fragment? = null
    var sharedPrefs : SharedPreferences? = null
    var navigationView : NavigationView? = null
    var anim : AnimationDrawable? = null
    var lastOn : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
/*        if (isAtleastLollipop()) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    showMessageOKCancel("You need to allow access to the device storage",
                            DialogInterface.OnClickListener { _, _ -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 200) })
                }
                else{
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 200)
                }
            }
        }*/


        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        //region
        //val fab = findViewById(R.id.fab_add) as FloatingActionButton
        //fab.setOnClickListener { view ->
        //    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //            .setAction("Action", null).show()
        //}
        //endregion
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigationView = findViewById(R.id.nav_view) as NavigationView
        val navHeader : LinearLayout = (navigationView as NavigationView).getHeaderView(0) as LinearLayout
        anim =  navHeader.background as AnimationDrawable
        anim?.setEnterFadeDuration(4000)
        anim?.setExitFadeDuration(4000)
        if (savedInstanceState == null) {
            val fragment : Fragment = AllNotes()
            val ft = fragmentManager.beginTransaction()
            checkCorrectMenuitem()
            ft.replace(R.id.content_frame, fragment)
            ft.commit()
        }
        if(sharedPrefs?.getBoolean("sp_noti_enabled", true) == false) {
            showMessageDisabled(view = findViewById(R.id.content_frame))
        }
        navigationView!!.setNavigationItemSelectedListener(this)

    }

     fun writeToFile(data: String) {
        try {
            val path : String = Environment.getExternalStorageDirectory().path + File.separator  + "NOTIFICATION"
            val folder : File = File(path)
            folder.mkdirs()
            val file : File = File(folder, "log.txt")
            val stream = FileOutputStream(file)
            stream.use { _ ->
                stream.write(data.toByteArray())
            }
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }
    fun readFromFile(): String {
        val path : String = Environment.getExternalStorageDirectory().path + File.separator  + "NOTIFICATION"
        val folder : File = File(path)
        folder.mkdirs()
        val file : File = File(folder, "log.txt")
        val length = file.length().toInt()

        val bytes = ByteArray(length)

        val inp = FileInputStream(file)
        inp.use { _ ->
            inp.read(bytes)
        }

        val contents = String(bytes)
        return contents
    }
    override fun onResume() {
        super.onResume()
        if (anim != null && !(anim as AnimationDrawable).isRunning)
            (anim as AnimationDrawable).start()
    }

    override fun onPause() {
        super.onPause()
        if (anim != null && (anim as AnimationDrawable).isRunning)
            (anim as AnimationDrawable).stop()
    }
    fun showMessageDisabled(view : View){
        val snackDisabled : Snackbar? = Snackbar.make(view, "Notifications are currently disabled.", Snackbar.LENGTH_INDEFINITE)
        snackDisabled?.setActionTextColor(Color.WHITE)
        snackDisabled?.setAction("Enable", { enableAll(); snackDisabled.dismiss(); Snackbar.make(view, "Notifications have been enabled", Snackbar.LENGTH_SHORT).show()})?.show()

    }

    fun enableAll(){
        sharedPrefs!!.edit().putBoolean("sp_noti_enabled", true).apply()
        createNotificationAlarm(sharedPrefs!!.getBoolean("sp_random", true), sharedPrefs!!.getString("etp_interval", "5").toInt())
    }
    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }


    fun checkCorrectMenuitem(){
        when (lastOn) {
            0 -> {
                navigationView?.setCheckedItem(R.id.nav_allNotes)
                supportActionBar?.title = "All notes"
            }
            1 -> {
                navigationView?.setCheckedItem(R.id.nav_intervalNotes)
                supportActionBar?.title = "Revision Notes"
            }
            2 -> {
                navigationView?.setCheckedItem(R.id.nav_reminder)
                supportActionBar?.title = "Reminders"
            }
            3 -> {
                navigationView?.setCheckedItem(R.id.nav_settings)
            }
            4 -> {
                navigationView?.setCheckedItem(R.id.nav_about)
                supportActionBar?.title = "About"
            }
        }
    }
    fun cancelNotificationAlarm(){
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(this, IntervalNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0)
        //Toast.makeText(baseContext, "Cancelled all", Toast.LENGTH_SHORT).show()
        alarmManager.cancel(pendingIntent)
    }

    fun cancelReminderAlarm(note : ReminderNote){
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(this, ReminderNotificationReceiver::class.java)
        myIntent.putExtra("reminderID", note.Id)
        val pendingIntent = PendingIntent.getBroadcast(this, note.Time.toInt(), myIntent, 0)
        alarmManager.cancel(pendingIntent)
    }
    fun createNotificationAlarm(sp_random: Boolean, interval_s: Int = 5){
        val alarmManager = baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
        val receiver : ComponentName = ComponentName(applicationContext, IntervalNotificationReceiver::class.java)
        val pm : PackageManager = applicationContext.packageManager
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)
    }
    fun createNotificationAlarm(){
        val alarmManager = baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(baseContext, IntervalNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(baseContext, 0, myIntent, 0)
        val interval: Int = sharedPrefs?.getString("etp_interval", "5")!!.toInt()
        val sp_enabled = sharedPrefs?.getBoolean("sp_noti_enabled", true)
        //Toast.makeText(baseContext, "No args interval: $interval", Toast.LENGTH_SHORT).show()
        if(sp_enabled == true){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + (1000 * 60 * interval).toLong(),
                    (1000 * 60 * interval).toLong(), pendingIntent)
        }
        val receiver : ComponentName = ComponentName(applicationContext, IntervalNotificationReceiver::class.java)
        val pm : PackageManager = applicationContext.packageManager
        pm.setComponentEnabledSetting(receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP)

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 200){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                //if we don't get the right permissions, exit the app
                Toast.makeText(applicationContext, "Permission denied. Exiting", Toast.LENGTH_LONG).show()
                this.finishAffinity()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /*    override fun onOptionsItemSelected(item: MenuItem): Boolean {
           // Handle action bar item clicks here. The action bar will
           // automatically handle clicks on the Home/Up button, so long
           // as you specify a parent activity in AndroidManifest.xml.
          when (item.itemId) {
               R.id.action_settings -> {
                   val fm : android.app.FragmentManager? = fragmentManager
                   val ft : android.app.FragmentTransaction? = fm?.beginTransaction()
                   settingsOption = item
                   settingsOption?.isEnabled = false
                   ft?.replace(R.id.content_frame, PreferencesFragment())?.addToBackStack(null)?.commit()
                   navigationView?.setCheckedItem(R.id.menu_none)
                   return true
               }
               else -> return super.onOptionsItemSelected(item)
           }
       } */


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        var title : String = getString(R.string.app_name)

        when (item.itemId) {
            R.id.nav_allNotes -> {
                fragment = AllNotes()
                title = "All notes"
                lastOn = 0
            }
            R.id.nav_intervalNotes -> {
                fragment = IntervalNotes()
                title = "Revision Notes"
                lastOn = 1
            }
            R.id.nav_reminder -> {
                fragment = RemindersFragment()
                title = "Reminders"
                lastOn = 2
            }
            R.id.nav_settings -> {
                fragment = PreferencesFragment()
                title = "Settings"
            }
            R.id.nav_about -> {
                fragment = AboutFragment()
                title = "About"
                lastOn = 4
            }
        }
        if(fragment != null){
            val ft = fragmentManager.beginTransaction()
            ft.replace(R.id.content_frame, fragment)
            if(fragment is PreferencesFragment){
                if(fragmentManager.backStackEntryCount <= 0){
                    ft.addToBackStack("Settings")
                }

            }
            ft.commit()
        }
        if (supportActionBar != null) {
            supportActionBar?.title = title
        }
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }
        if(fragmentManager.backStackEntryCount > 0){
            checkCorrectMenuitem()
            fragmentManager.popBackStack()
/*            settingsOption?.isEnabled = true*/
        }else {
            super.onBackPressed()
        }

    }

    override fun onDestroy() {
        val dbH : DatabaseHandler = DatabaseHandler(baseContext)
        dbH.close()
        super.onDestroy()
    }
    fun isAtleastLollipop() : Boolean{
        return Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP
    }
}
