package com.jmulla.notificationnotes

/**
 * Created by Jamal on 07/07/2017.
 */
data class ReminderNote(var Title : String, var Content : String, var Time : Long, var Id : String){
    constructor() : this("", "", 0, "")
}