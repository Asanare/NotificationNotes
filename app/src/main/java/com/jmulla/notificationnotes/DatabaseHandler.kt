package com.jmulla.notificationnotes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


/**
 * Created by Jamal on 30/06/2017.
 */

internal class DatabaseHandler(myContext: Context) : SQLiteOpenHelper(myContext, DatabaseHandler.DATABASE_NAME, null, DatabaseHandler.DATABASE_VERSION) {
    companion object {
        // Table names
        val TABLE_INTERVAL = "IntervalTable"
        val TABLE_REMINDERS = "RemindersTable"
        // Database Version
        private val DATABASE_VERSION = 1
        // Database Name
        private val DATABASE_NAME = "MainDatabase"
        // Interval Notes Table Column names
        private val I_ID_KEY = "primary_id"
        private val I_TITLE_KEY = "title"
        private val I_CONTENT_KEY = "i_content"
        private val I_ENABLED_KEY = "enabled"
        private val I_ID = "id"
        // Reminders  Table Column names
        private val R_ID_KEY = "primary_id"
        private val R_TITLE_KEY = "title"
        private val R_CONTENT_KEY = "i_content"
        private val R_TIME_KEY = "time"
        private val R_ID = "id"


    }
    // Creating Tables
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_INTERVAL_TABLE = "CREATE TABLE $TABLE_INTERVAL($I_ID_KEY INTEGER PRIMARY KEY AUTOINCREMENT,$I_TITLE_KEY TEXT, $I_CONTENT_KEY TEXT, $I_ENABLED_KEY INTEGER, $I_ID TEXT);"
        val CREATE_REMINDERS_TABLE = "CREATE TABLE $TABLE_REMINDERS($R_ID_KEY INTEGER PRIMARY KEY AUTOINCREMENT,$R_TITLE_KEY TEXT, $R_CONTENT_KEY TEXT, $R_TIME_KEY INTEGER, $R_ID TEXT);"
        db.execSQL(CREATE_INTERVAL_TABLE)
        db.execSQL(CREATE_REMINDERS_TABLE)
        Log.d("DB STATUS: ", "CREATED")
    }

    // Upgrading database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INTERVAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REMINDERS")
        // Create tables again
        onCreate(db)
    }



    //add interval intervalNote to table
    fun addIntervalNote(intervalNote: IntervalNote) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(I_TITLE_KEY, intervalNote.Title)
        values.put(I_CONTENT_KEY, intervalNote.Content)
        values.put(I_ENABLED_KEY, intervalNote.Enabled)
        values.put(I_ID, intervalNote.Id)
        // Inserting Row
        db.insert(TABLE_INTERVAL, null, values)
        // Closing database connection
        db.close()
    }

    //add reminder to table
    fun addReminder(note : ReminderNote) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(R_TITLE_KEY, note.Title)
        values.put(R_CONTENT_KEY, note.Content)
        values.put(R_TIME_KEY, note.Time)
        values.put(R_ID, note.Id)
        // Inserting Row
        db.insert(TABLE_REMINDERS, null, values)
        // Closing database connection
        db.close()
    }



    // Getting single interval note
    fun getIntervalNote(id: String): IntervalNote {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_INTERVAL, arrayOf(I_ID_KEY, I_TITLE_KEY, I_CONTENT_KEY, I_ENABLED_KEY, I_ID), "$I_ID =?", arrayOf(id), null, null, null, null)
        cursor.moveToFirst()
        val note = IntervalNote(cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(3))
        cursor.close()
        db.close()
        return note
    }

    // Getting single reminder note
    fun getReminderNote(id: String): ReminderNote {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_REMINDERS, arrayOf(R_ID_KEY, R_TITLE_KEY, R_CONTENT_KEY, R_TIME_KEY, R_ID), "$R_ID =?", arrayOf(id), null, null, null, null)
        var note: ReminderNote = ReminderNote()
        if (cursor.moveToFirst()) {
            do {
                note = ReminderNote(cursor.getString(1), cursor.getString(2), cursor.getString(3).toLong(), cursor.getString(4))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return note
    }


    fun getAllIntervalNotes(tableName: String): MutableList<kotlin.Any> {
        val notesList = ArrayList<kotlin.Any>()
        // Select All Query
        val selectQuery = "SELECT * FROM " + tableName

        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                val note = IntervalNote(cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4))
                notesList.add(note)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return notesList
    }
    fun getAllReminders(): MutableList<kotlin.Any> {
        val notesList = ArrayList<kotlin.Any>()
        // Select All Query
        val selectQuery = "SELECT * FROM $TABLE_REMINDERS"

        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                val note = ReminderNote(cursor.getString(1), cursor.getString(2), cursor.getString(3).toLong(), cursor.getString(4))
                notesList.add(note)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return notesList
    }


    // Get the number of records in a table
    fun getCount(tableName: String): Int {
        val countQuery = "SELECT  * FROM " + tableName
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        val count = cursor.count
        cursor.close()
        db.close()
        return count
    }


    fun updateReminderNote(note : ReminderNote){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(R_TITLE_KEY, note.Title)
        values.put(R_CONTENT_KEY, note.Content)
        values.put(R_TIME_KEY, note.Time)
        values.put(R_ID, note.Id)
        db.update(TABLE_REMINDERS, values, R_ID + " = ?",
                arrayOf(note.Id))
        db.close()
    }

    fun updateIntervalNote(note : IntervalNote){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(I_TITLE_KEY, note.Title)
        values.put(I_CONTENT_KEY, note.Content)
        values.put(I_ENABLED_KEY, note.Enabled)
        values.put(I_ID, note.Id)
        db.update(TABLE_INTERVAL, values, I_ID + " = ?",
                arrayOf(note.Id))
        db.close()
    }
    /*
        //updating a playlist
        fun updatePlaylist(playlist: PlaylistModel): Int {
            val db = this.writableDatabase

            val values = ContentValues()
            values.put(KEY_PLAYLISTS_NAME, playlist.getName())
            values.put(KEY_PLAYLISTS_TRACKS, playlist.getNumberOfTracks())
            val gson = Gson()
            val jsonSongs = gson.toJson(playlist.getSongs())
            values.put(KEY_PLAYLISTS_SONGS, jsonSongs)
            values.put(KEY_PLAYLIST_ID, playlist.getId())
            // updating row
            return db.update(TABLE_PLAYLIST, values, KEY_PLAYLISTS_ID + " = ?",
                    arrayOf<String>())
        }

        //add a song to a specific playlist
        fun addSongToPlaylist(playlist: PlaylistModel, song: Song) {
            val db = this.writableDatabase
            val songArrayList = getPlaylist(playlist.getId()).getSongs()
            for (loopSong in songArrayList) {
                if (loopSong.id.equals(song.id)) {
                    return
                }
            }
            songArrayList.add(song)
            val gson = Gson()
            val jsonSongs = gson.toJson(songArrayList)
            // updating row
            val c = db.rawQuery("UPDATE " + TABLE_PLAYLIST + " SET " + KEY_PLAYLISTS_SONGS + " = " + "'" + jsonSongs + "'" + " WHERE " + KEY_PLAYLIST_ID + " = " + "'" + playlist.getId() + "'", null)
            CurrentSong.makeToast(myContext, c.count)
            c.close()

        }

    */
    fun deleteNote(id : String, tableName: String) {
        val db = this.writableDatabase
        db.delete(tableName, I_ID + " = ?",
                arrayOf(id))
        db.close()
    }
    fun deleteReminder(id : String, tableName: String) {
        val db = this.writableDatabase
        db.delete(tableName, R_ID + " = ?",
                arrayOf(id))
        db.close()
    }
/*
    //deleting a specific playlist
    fun deletePlaylist(playlistModel: PlaylistModel) {
        val db = this.writableDatabase
        db.delete(TABLE_PLAYLIST, KEY_PLAYLIST_ID + " = ?",
                arrayOf<String>(String.valueOf(playlistModel.getId())))

    }

    //deleting a table
    fun deleteTable(tableName: String) {
        val db = this.writableDatabase
        db.rawQuery("DELETE FROM " + tableName, null)

    }*/




}
