package com.appttude.h_mal.days_left_kotlin

import android.app.Activity
import android.widget.ProgressBar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PreviouslyUsedItemsClass(progressBar: ProgressBar, activity: Activity) : ValueEventListener {

    override fun onCancelled(p0: DatabaseError) {

    }

    override fun onDataChange(p0: DataSnapshot) {
        p0.key
    }
}