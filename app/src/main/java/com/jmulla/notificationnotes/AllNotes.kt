package com.jmulla.notificationnotes

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


/**
 * Created by Jamal on 18/06/2017.
 */
class AllNotes : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews()
        return inflater?.inflate(R.layout.allnotes_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val recyclerView = view?.findViewById(R.id.rv_all_notes) as RecyclerView
        val tv_no_notes = view.findViewById(R.id.tv_no_notes) as TextView
        val allNotes = getNotes()
        if (allNotes.size == 0){
            tv_no_notes.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }else{
            tv_no_notes.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        val mAdapter = NoteAdapter(allNotes)
        val mLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mAdapter
        recyclerView.isLongClickable = true
/*        val mDividerItemDecoration = DividerItemDecoration(recyclerView.context, mLayoutManager.orientation)
        recyclerView.addItemDecoration(mDividerItemDecoration)*/
        super.onViewCreated(view, savedInstanceState)
    }


    fun getNotes(): MutableList<kotlin.Any> {
        val dbH : DatabaseHandler = DatabaseHandler(activity.applicationContext)
        val intervalNotes: MutableList<kotlin.Any> = dbH.getAllIntervalNotes(DatabaseHandler.TABLE_INTERVAL)
        val reminderNotes: MutableList<kotlin.Any> = dbH.getAllReminders()
        val allNotes : MutableList<kotlin.Any> = (intervalNotes + reminderNotes) as MutableList<kotlin.Any>
        return allNotes
    }

}