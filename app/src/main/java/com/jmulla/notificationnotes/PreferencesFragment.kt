package com.jmulla.notificationnotes

import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Jamal on 21/06/2017.
 */
class PreferencesFragment : PreferenceFragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        val sp_random : SwitchPreference = this.findPreference("sp_random") as SwitchPreference
        val sp_enabled : SwitchPreference = this.findPreference("sp_noti_enabled") as SwitchPreference
        val intervalPreference : EditTextPreference = this.findPreference("etp_interval") as EditTextPreference
        val mActivity : MainActivity = (activity as MainActivity)

        val thread = object : Thread(){
            override fun run() {
                if(!sp_enabled.isChecked){
                    sp_random.isEnabled = false
                    intervalPreference.isEnabled = false
                }
                sp_random.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                    intervalPreference.isEnabled = !(newValue as Boolean)
                    if(newValue == true){
                        //Toast.makeText(activity, "Random interval", Toast.LENGTH_SHORT).show()
                        mActivity.createNotificationAlarm(newValue)
                    }else{
                        //Toast.makeText(activity, "Normal interval", Toast.LENGTH_SHORT).show()
                        mActivity.createNotificationAlarm(newValue, intervalPreference.text.toInt())
                    }
                    true
                }
                sp_enabled.onPreferenceChangeListener = Preference.OnPreferenceChangeListener{preference, newValue ->
                    if(newValue as Boolean == true){
                        //Toast.makeText(activity, "Notifications have been enabled", Toast.LENGTH_SHORT).show()
                        Snackbar.make(view, "Notifications have been enabled", Snackbar.LENGTH_SHORT).show()
                        mActivity.createNotificationAlarm(sp_random.isChecked, intervalPreference.text.toInt())
                        sp_random.isEnabled = true
                        intervalPreference.isEnabled = true

                    }else {
                        //Toast.makeText(activity, "Notifications have been disabled", Toast.LENGTH_SHORT).show()
                        Snackbar.make(view, "Notifications have been disabled", Snackbar.LENGTH_SHORT).show()
                        mActivity.cancelNotificationAlarm()
                        sp_random.isEnabled = false
                        intervalPreference.isEnabled = false
                        mActivity.showMessageDisabled(activity.findViewById(R.id.content_frame))
                    }
                    true
                }
                intervalPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                    mActivity.createNotificationAlarm(sp_random.isChecked, (newValue as String).toInt())
                    true
                }
                super.run()
            }

        }
        thread.start()

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}