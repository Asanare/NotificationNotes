package com.jmulla.notificationnotes

import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.*




/**
 * Created by Jamal on 18/06/2017.
 */
class RemindersFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews()
        return inflater?.inflate(R.layout.reminders_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val tv_edit_time : TextView = view.findViewById(R.id.tv_select_time) as TextView
        val tv_edit_date : TextView = view.findViewById(R.id.tv_select_date) as TextView
        val et_reminders_title : EditText = view.findViewById(R.id.et_reminders_title) as EditText
        val et_reminders_content : EditText = view.findViewById(R.id.et_reminders_content) as EditText
        tv_edit_time.isEnabled = false
        val btn_addReminder : Button = view.findViewById(R.id.btn_add_reminders_note) as Button
        val alertSnack : Snackbar = Snackbar.make(view, "Invalid date/time", Snackbar.LENGTH_INDEFINITE)
        val snackView : View = alertSnack.view
        val snackText : TextView = snackView.findViewById(android.support.design.R.id.snackbar_text) as TextView
        val alertDrawable : Drawable = ContextCompat.getDrawable(activity, android.R.drawable.ic_dialog_alert)
        snackText.setCompoundDrawablesRelativeWithIntrinsicBounds(alertDrawable, null, null, null)
        btn_addReminder.isEnabled = false
        snackText.gravity = Gravity.CENTER
        var setDate : String? = null
        var isBefore : Boolean? = null
        var isToday : Boolean? = null
        var titleOK : Boolean = false
        var contentOK : Boolean = true
        var dateOK : Boolean = false
        var timeOK : Boolean = false

        var day : Int = 0
        var month : Int = 0
        var yearSet : Int = 0
        var hour : Int = 0
        var minute : Int = 0
        et_reminders_title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                titleOK = !et_reminders_title.text.isEmpty() && (s.length <= 40)
                btn_addReminder.isEnabled = titleOK && contentOK && dateOK && timeOK
            }
        })
        et_reminders_content.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                contentOK = (s.length <= 40)
                btn_addReminder.isEnabled = titleOK && contentOK && dateOK && timeOK
            }
        })
        val dpd = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            setDate = dayOfMonth.toString() + "/" + (monthOfYear + 1).toString() + "/" + year
            val otherCalender = Calendar.getInstance()
            otherCalender.set(Calendar.YEAR, year)
            otherCalender.set(Calendar.MONTH, monthOfYear)
            otherCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateSpecified = otherCalender.time
            isToday = DateUtils.isToday(otherCalender.timeInMillis)
            if(dateSpecified.before(Calendar.getInstance().time) && !isToday!!){
                tv_edit_date.text = setDate
                tv_edit_date.setTextColor(Color.RED)
                snackText.text = "Invalid Date"
                tv_edit_time.isEnabled = false
                dateOK = false
                alertSnack.show()
                btn_addReminder.isEnabled = false
                isBefore = true
            }else{
                tv_edit_time.isEnabled = true
                if(tv_edit_time.currentTextColor == Color.RED && isToday == true){
                    tv_edit_date.setTextColor(Color.BLACK)
                    snackText.text = "Invalid Time"
                    alertSnack.show()
                    dateOK = false
                }else{
                    tv_edit_date.setTextColor(Color.BLACK)
                    tv_edit_date.text = setDate
                    tv_edit_time.setTextColor(Color.BLACK)
                    alertSnack.dismiss()
                    dateOK = true
                    day = dayOfMonth
                    month = monthOfYear
                    yearSet = year
                    if(titleOK && contentOK && dateOK && timeOK){
                        btn_addReminder.isEnabled = true
                    }


                }
                isBefore = false
            }
        }


        tv_edit_date.setOnClickListener {
            val d = DatePickerDialog(activity, dpd, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            d.show()
        }

        val timePickerListener = TimePickerDialog.OnTimeSetListener{

            timePicker, selectedHour, selectedMinute ->
            if(isBefore == null){
                tv_edit_time.text = "Select a date first"
            }
            if((isBefore == true || isToday == true)){
                var before=false
                val calendar = Calendar.getInstance()
                if(selectedHour < calendar.get(Calendar.HOUR_OF_DAY))
                    before=true
                else if(selectedHour == calendar.get(Calendar.HOUR_OF_DAY)){
                    if(selectedMinute <= calendar.get(Calendar.MINUTE))
                        before=true
                }
                if(before){
                    tv_edit_time.text = String.format("%02d:%02d", selectedHour, selectedMinute)
                    tv_edit_time.setTextColor(Color.RED)
                    snackText.text = "Invalid Time"
                    timeOK = false
                    alertSnack.show()
                    btn_addReminder.isEnabled = false
                }else{
                    if(isToday == true){
                        alertSnack.dismiss()
                        tv_edit_time.setTextColor(Color.BLACK)
                        timeOK = true
                        hour = selectedHour
                        minute = selectedMinute
                        if(titleOK && contentOK && dateOK && timeOK){
                            btn_addReminder.isEnabled = true
                        }
                    }
                    else{
                        tv_edit_time.setTextColor(Color.RED)
                        snackText.text = "Invalid Date"
                        dateOK = false
                        btn_addReminder.isEnabled = false
                        alertSnack.show()
                    }
                    tv_edit_time.text = String.format("%02d:%02d", selectedHour, selectedMinute)
                }

            }else{
                tv_edit_time.text = String.format("%02d:%02d", selectedHour, selectedMinute)
                tv_edit_time.setTextColor(Color.BLACK)
                timeOK = true
                hour = selectedHour
                minute = selectedMinute
                alertSnack.dismiss()
                if(titleOK && contentOK && dateOK && timeOK){
                    btn_addReminder.isEnabled = true
                }
            }
        }

        tv_edit_time.setOnClickListener {
            val mTimePicker: TimePickerDialog = TimePickerDialog(activity, timePickerListener, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE) + 1, true)
            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }
        
        btn_addReminder.setOnClickListener {
            val setCalender = Calendar.getInstance()
            setCalender.set(yearSet, month, day, hour, minute)
            val id = generateId()
            val note : ReminderNote = ReminderNote(et_reminders_title.text.toString(), et_reminders_content.text.toString(), setCalender.timeInMillis, id)
            Log.i("ID at birth" , id)
            et_reminders_title.setText("")
            et_reminders_content.setText("")
            createReminder(note)
        }
    }

    //generates a random id
    fun generateId(): String {
        return UUID.randomUUID().toString()
    }
    fun createReminder(note : ReminderNote){
        val dbH : DatabaseHandler = DatabaseHandler(activity.applicationContext)
        dbH.addReminder(note)
        createReminderAlarm(note.Time, note.Id)
        Toast.makeText(activity, "Reminder has been set", Toast.LENGTH_SHORT).show()

    }


    fun createReminderAlarm(time : Long, id : String){
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(activity, ReminderNotificationReceiver::class.java)
        myIntent.putExtra("reminderID", id)
        val pendingIntent = PendingIntent.getBroadcast(activity, time.toInt(), myIntent, 0)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    time, pendingIntent)


    }
}