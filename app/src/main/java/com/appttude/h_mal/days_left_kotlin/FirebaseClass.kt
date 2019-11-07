package com.appttude.h_mal.days_left_kotlin

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseClass {
    companion object {
        val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference()
        var auth: FirebaseAuth = FirebaseAuth.getInstance()

        val USER_FIREBASE = "users"
        val EMPLOYER_FIREBASE = "employers"
        val SHIFT_FIREBASE = "shifts"
        val TASK_FIREBASE = "taskList"

        val SHIFT_ID = "shift_id"

        val PIECE = "Piece Rate"
        val HOURLY = "Hourly"

    }

}