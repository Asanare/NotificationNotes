package com.jmulla.notificationnotes

import android.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.*




/**
 * Created by Jamal on 17/06/2017.
 */
class IntervalNotes : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews()
        return inflater?.inflate(R.layout.interval_fragment, container, false)
    }

    //generates a random id
    fun generateId(): String {
        return UUID.randomUUID().toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        retainInstance = true
        super.onCreate(savedInstanceState)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val btn_addNote : Button? = view?.findViewById(R.id.btn_add_interval_note) as Button
        btn_addNote?.isEnabled = false
        var titleOK = false
        var contentOK = true
        val etTitle : EditText? = view?.findViewById(R.id.et_interval_title) as EditText
        val etContent : EditText? = view?.findViewById(R.id.et_interval_content) as EditText

        etTitle?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                titleOK = !etTitle.text.isEmpty() && (s.length <= 40)
                btn_addNote?.isEnabled = titleOK && contentOK
            }
        })
        etContent?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                contentOK = (s.length <= 40)
                btn_addNote?.isEnabled = titleOK && contentOK
            }
        })
        btn_addNote?.setOnClickListener{
                val intervalNote: IntervalNote = IntervalNote(etTitle?.text.toString(), etContent?.text.toString(), 1, generateId())
                addToDB(intervalNote)
                etTitle?.setText("")
                etTitle?.hasFocus()
                etContent?.clearFocus()
                etContent?.setText("")
        }
/*        for (i in 1..10000){
            addToDB(IntervalNote("Test", "Content", 1, generateId()))
        }*/


    }
    fun addToDB(intervalNote: IntervalNote){
        val dbH : DatabaseHandler = DatabaseHandler(activity.applicationContext)
        dbH.addIntervalNote(intervalNote)
        if(dbH.getCount("IntervalTable") == 1){
            (activity as MainActivity).createNotificationAlarm()
        }
        //Snackbar.make(view, "Added Successfully", Snackbar.LENGTH_SHORT).show()
        Toast.makeText(activity, "Added", Toast.LENGTH_SHORT).show()
    }






}