package com.jmulla.notificationnotes
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*






class NoteAdapter(private val notesList: MutableList<kotlin.Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var alert : AlertDialog? = null
    private val INTERVAL : Int = 0
    private val REMINDER : Int = 1
    //To add different types to the recycler view, you will have to get rid of the List<IntervalNote> and use List<Object> with the dividers in the list. Refer to music player example
    private val dataset : MutableList<kotlin.Any> = notesList
    inner class IntervalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var i_title: TextView = view.findViewById(R.id.tv_intervalTitle) as TextView
        var i_content: TextView = view.findViewById(R.id.tv_intervalContent) as TextView
        var i_edit_btn : Button = view.findViewById(R.id.btn_edit_interval) as Button
        var i_delete_btn : Button = view.findViewById(R.id.btn_delete_interval) as Button
        var s_enabled : Switch = view.findViewById(R.id.switchIntervalEnabled) as Switch
    }

    inner class ReminderViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var r_title : TextView = view.findViewById(R.id.tv_reminder_title) as TextView
        var r_content : TextView = view.findViewById(R.id.tv_reminder_content) as TextView
        var r_date : TextView = view.findViewById(R.id.tv_reminder_date) as TextView
        var r_time : TextView = view.findViewById(R.id.tv_reminder_time) as TextView
        var r_edit_btn : Button = view.findViewById(R.id.btn_edit_reminder) as Button
        var r_cancel_btn : Button = view.findViewById(R.id.btn_cancel_reminder) as Button
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder?) {
        super.onViewAttachedToWindow(holder)
        //Toast.makeText(holder?.itemView?.context, "ATTACHED", Toast.LENGTH_LONG).show()
        LocalBroadcastManager.getInstance(holder?.itemView?.context).registerReceiver(mMessageReceiver,
                IntentFilter("refresh"))
    }
    val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val id = intent.getStringExtra("id")
            var note : ReminderNote? = null
            for(n in dataset){
                if(n is IntervalNote){
                    Log.i("other id:", n.Id)
                }else{
                    if((n as ReminderNote).Id == id){
                        note = n
                    }
            }

            }
            if(note !=null){
                dataset.remove(note)
                //Toast.makeText(context, "NOT NULL", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged()
            }


        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder?) {
        super.onViewDetachedFromWindow(holder)
        //Toast.makeText(holder?.itemView?.context, "DETACHED", Toast.LENGTH_LONG).show()
        LocalBroadcastManager.getInstance(holder?.itemView?.context).unregisterReceiver(mMessageReceiver)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            INTERVAL -> {
                val v1 = inflater.inflate(R.layout.interval_note_layout, parent, false)
                viewHolder = IntervalViewHolder(v1)
            }
            REMINDER -> {
                val v2 = inflater.inflate(R.layout.reminder_note_layout, parent, false)
                viewHolder = ReminderViewHolder(v2)
            }
            else -> {
                val v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                viewHolder = IntervalViewHolder(v)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == INTERVAL){
            val ivh : IntervalViewHolder = holder as IntervalViewHolder
            configureIntervalVH(ivh, position)
        }else{
            val rvh : ReminderViewHolder = holder as ReminderViewHolder
            configureReminderVH(rvh, position)
        }

    }

    private fun showEditDialog(context: Context, title : String, content : String, IntervalOrReminder : Int, note : kotlin.Any, position : Int) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = (context as MainActivity).layoutInflater
        val dialogView = inflater.inflate(R.layout.edit_dialog, null)
        dialogBuilder.setView(dialogView)
        val title_et = dialogView.findViewById(R.id.et_edit_title) as EditText
        val content_et = dialogView.findViewById(R.id.et_edit_content) as EditText
        var btn_OK : Button = Button(context)
        title_et.setText(title)
        content_et.setText(content)
        var titleOK : Boolean = true
        var contentOK : Boolean = true
        title_et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                titleOK = !title_et.text.isEmpty() && (s.length <= 40)
                btn_OK.isEnabled = titleOK && contentOK
            }
        })
        content_et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                contentOK = (s.length <= 40)
                btn_OK.isEnabled = titleOK && contentOK
            }
        })
/*        dialogBuilder.setTitle("Edit")
        dialogBuilder.setMessage("Enter text below")*/
        dialogBuilder.setPositiveButton("Done") { dialog, whichButton ->
            val dbH : DatabaseHandler = DatabaseHandler(context)
            if(IntervalOrReminder == INTERVAL){
                val oldIN : IntervalNote = note as IntervalNote
                val newTitle : String = title_et.text.toString()
                val newContent : String = content_et.text.toString()
                val newIN : IntervalNote = IntervalNote(newTitle, newContent, oldIN.Enabled, oldIN.Id)
                dbH.updateIntervalNote(newIN)
                (dataset[position] as IntervalNote).Title = newTitle
                (dataset[position] as IntervalNote).Content = newContent
                (dataset[position] as IntervalNote).Enabled = oldIN.Enabled
                this.notifyItemChanged(position)
                Snackbar.make(context.findViewById(R.id.content_frame), "Updated", Snackbar.LENGTH_SHORT).show()
            }else{
                val oldRN : ReminderNote = note as ReminderNote
                val newTitle : String = title_et.text.toString()
                val newContent : String = content_et.text.toString()
                val newRN : ReminderNote = ReminderNote(newTitle, newContent, oldRN.Time, oldRN.Id)
                dbH.updateReminderNote(newRN)
                (dataset[position] as ReminderNote).Title = newTitle
                (dataset[position] as ReminderNote).Content = newContent
                this.notifyDataSetChanged()
                Snackbar.make(context.findViewById(R.id.content_frame), "Updated", Snackbar.LENGTH_SHORT).show()
            }
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, whichButton ->
            //pass
        }
        val b = dialogBuilder.create()
        b.show()
        btn_OK = (b as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        btn_OK.isEnabled = titleOK && contentOK
    }
    private fun configureReminderVH(holderReminder: ReminderViewHolder, position: Int) {
        val note = notesList[position] as ReminderNote
        holderReminder.r_title.text = note.Title
        holderReminder.r_content.text = note.Content
        val datetime = Calendar.getInstance()
        datetime.timeInMillis = note.Time
        val day = datetime.get(Calendar.DAY_OF_MONTH)
        val month = datetime.get(Calendar.MONTH)
        val year = datetime.get(Calendar.YEAR)
        val hour = datetime.get(Calendar.HOUR_OF_DAY)
        val minute = datetime.get(Calendar.MINUTE)
        holderReminder.r_date.text = "$day/${(month + 1)}/$year"
        holderReminder.r_time.text = String.format("%02d:%02d", hour, minute)
        holderReminder.r_edit_btn.setOnClickListener {
            showEditDialog(holderReminder.itemView.context, note.Title, note.Content, REMINDER, note, position)
        }
        holderReminder.r_cancel_btn.setOnClickListener {
            cancelReminder(note, holderReminder.itemView.context, holderReminder.itemView)
        }
        holderReminder.itemView.setOnClickListener{
            v ->
            val pos : Int = holderReminder.adapterPosition
            val n : ReminderNote = getItem(pos) as ReminderNote
        }
    }

    private fun configureIntervalVH(holderInterval: IntervalViewHolder, position: Int) {
        val note = notesList[position] as IntervalNote
        holderInterval.i_title.text = note.Title
        holderInterval.i_content.text = note.Content
        holderInterval.s_enabled.isChecked = (note.Enabled == 1)
        holderInterval.i_edit_btn.setOnClickListener {
            showEditDialog(holderInterval.itemView.context, note.Title, note.Content, INTERVAL, note, position)
        }
        holderInterval.i_delete_btn.setOnClickListener {
            v ->
            val pos : Int = holderInterval.adapterPosition
            val n : IntervalNote = getItem(pos) as IntervalNote
            deleteNote(n, v.context, holderInterval.itemView)
        }
        holderInterval.s_enabled.setOnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
            val dbH : DatabaseHandler = DatabaseHandler(holderInterval.itemView.context)
                if(holderInterval.s_enabled.isChecked){
                    val newNote : IntervalNote = IntervalNote(note.Title, note.Content, 1, note.Id)
                    dbH.updateIntervalNote(newNote)
                    (dataset[position] as IntervalNote).Enabled = 1
                    //Snackbar.make(holderInterval.itemView, "This note has been enabled", Snackbar.LENGTH_LONG).show()
                }
                else{
                    val newNote : IntervalNote = IntervalNote(note.Title, note.Content, 0, note.Id)
                    dbH.updateIntervalNote(newNote)
                    (dataset[position] as IntervalNote).Enabled = 0
                    //Snackbar.make(holderInterval.itemView, "This note has been disabled", Snackbar.LENGTH_LONG).show()
                }
        }

    }


    private fun createDialog(title: String, message: String, positive: String, negative: String, cancelable: Boolean, func: Runnable, context : Context) {
        val dialog = AlertDialog.Builder(context)
        dialog.setCancelable(cancelable)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setPositiveButton(positive)
        { _, _ -> func.run()
        }.setNegativeButton(negative) { _, _ ->
        }
        alert = dialog.create()
        alert?.show()
    }



    fun deleteNote(n : IntervalNote, context : Context, view : View){
        val dbH : DatabaseHandler? = DatabaseHandler(context.applicationContext)
        createDialog("Delete note", "Delete this note permanently?", "Delete", "Cancel", true, Runnable {dbH?.deleteNote(n.Id, DatabaseHandler.TABLE_INTERVAL);dataset.remove(n);this.notifyDataSetChanged();Snackbar.make(view, "Deleted", Snackbar.LENGTH_SHORT).show()}, context)
        if(dbH?.getCount("IntervalTable")!! <=0){
            (context as MainActivity).cancelNotificationAlarm()
        }
    }

    fun cancelReminder(n : ReminderNote, context: Context, view: View){
        val dbH : DatabaseHandler? = DatabaseHandler(context.applicationContext)
        createDialog("Remove reminder", "Delete this reminder?", "Remove", "Cancel", true, Runnable {dbH?.deleteReminder(n.Id, DatabaseHandler.TABLE_REMINDERS);dataset.remove(n);this.notifyDataSetChanged();Snackbar.make(view, "Cancelled", Snackbar.LENGTH_SHORT).show()}, context)
        (context as MainActivity).cancelReminderAlarm(note = n)
    }
    private fun getItem(position: Int): kotlin.Any {
        return dataset[position]
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (dataset[position] is IntervalNote) {
            return INTERVAL
        } else if (dataset[position] is ReminderNote) {
            return REMINDER
        }
        return -1
    }
}

