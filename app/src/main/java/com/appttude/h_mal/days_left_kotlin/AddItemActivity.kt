package com.appttude.h_mal.days_left_kotlin

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.appttude.h_mal.days_left_kotlin.AddShiftActivity.Companion.EMPLOYERREQUEST
import com.appttude.h_mal.days_left_kotlin.AddShiftActivity.Companion.REQUEST
import com.appttude.h_mal.days_left_kotlin.AddShiftActivity.Companion.TASK_CONSTANT
import kotlinx.android.synthetic.main.activity_add_item.*

class AddItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        setSupportActionBar(toolbar)

        val requestCode = intent.getIntExtra(REQUEST, EMPLOYERREQUEST)

        if (requestCode == EMPLOYERREQUEST) {
            supportFragmentManager.beginTransaction().replace(R.id.container, AddEmployerFragment()).addToBackStack("Employer").commit()
        } else {
            val taskObject = intent.getSerializableExtra(TASK_CONSTANT)
            val addTaskFragment = AddTaskFragment()
            if (taskObject != null) {
                val bundle = Bundle()
                bundle.putSerializable(TASK_CONSTANT, taskObject)
                addTaskFragment.arguments = bundle
            }
            supportFragmentManager.beginTransaction().replace(R.id.container, addTaskFragment).addToBackStack("Task").commit()
        }
    }

    override fun onBackPressed() {

            AlertDialog.Builder(this)
                .setTitle("Leave?")
                .setMessage("Are you sure you return to previous?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener {
                        arg0, arg1 -> finish() })
                .create().show()

    }
}
