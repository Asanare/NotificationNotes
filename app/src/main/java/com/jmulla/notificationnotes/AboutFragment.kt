package com.jmulla.notificationnotes

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Jamal on 19/07/2017.
 */
class AboutFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews()
        return inflater?.inflate(R.layout.about_fragment, container, false)
    }
}