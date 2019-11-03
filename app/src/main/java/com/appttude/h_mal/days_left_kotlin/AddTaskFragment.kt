package com.appttude.h_mal.days_left_kotlin

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.appttude.h_mal.days_left_kotlin.AddShiftActivity.Companion.TASK_CONSTANT
import com.appttude.h_mal.days_left_kotlin.Objects.TaskObject
import kotlinx.android.synthetic.main.fragment_add_task.*
import java.lang.Float

class AddTaskFragment : Fragment() {
    lateinit var taskList: Array<String>
    lateinit var strings: Array<String>
    lateinit var workTypeArray: Array<String>
    var current = ""
    var previous = ""
    var product = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialiseArrayString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            val bundle = arguments
            val taskObject = bundle?.getSerializable(TASK_CONSTANT) as TaskObject
            spinner_one.setSelection(getSpinnerOneSelection(taskObject.workType!!))
            pay_rate.setText(taskObject.rate.toString())
            spinner_Two.setSelection(getSpinnerTwoSelection(taskObject.task!!))
        }

        setupSpinnerOne()
        setupSpinnerTwo()

        search_button.setOnClickListener(submit)
    }

    private fun initialiseArrayString() {
        taskList = resources.getStringArray(R.array.task_list)
        strings = arrayOf<String>(taskList[0], taskList[1] + product, taskList[2], taskList[3])
    }

    private fun setupSpinnerOne() {
        workTypeArray = resources.getStringArray(R.array.work_type)
        val spinnerArrayAdapter = object : ArrayAdapter<String>(
            context, android.R.layout.simple_spinner_item,
            workTypeArray
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }
                return view
            }


        }
        spinnerArrayAdapter.setDropDownViewResource(
            android.R.layout
                .simple_spinner_dropdown_item
        )
        spinner_one.adapter = spinnerArrayAdapter
        spinner_one.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    1 -> unit_text.text = "per Unit"
                    2 -> unit_text.text = "per Hour"
                    else -> {
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun setupSpinnerTwo() {
        val spinnerArrayAdapter = object : ArrayAdapter<String>(
            context, android.R.layout.simple_spinner_item,
            strings
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }
                return view
            }
        }
        spinnerArrayAdapter.setDropDownViewResource(
            android.R.layout
                .simple_spinner_dropdown_item
        )
        spinner_Two.adapter = spinnerArrayAdapter

        val listener = SpinnerInteractionListener()

        spinner_Two.onItemSelectedListener = listener
        spinner_Two.setOnTouchListener(listener)
    }

    inner class SpinnerInteractionListener : AdapterView.OnItemSelectedListener, View.OnTouchListener {

        internal var userSelect = false

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            userSelect = true
            return false
        }

        override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
            if (userSelect) {
                Toast.makeText(context, "current = $pos", Toast.LENGTH_SHORT).show()

                previous = current
                current = parent.getItemAtPosition(pos) as String

                if (pos == 1) {
                    val edittext = EditText(context)
                    edittext.hint = "Product Harvested?"
                    val builder = AlertDialog.Builder(context)
                    builder.setView(edittext)
                    builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                        spinner_Two.setSelection(getPosition(previous))
                        dialog.dismiss()
                    }.setPositiveButton(android.R.string.ok) { dialog, which ->
                        val text = edittext.text.toString()
                        if (!TextUtils.isEmpty(text)) {
                            product = " $text"
                            initialiseArrayString()
                            setupSpinnerTwo()
                            spinner_Two.setSelection(1)
                        }
                        dialog.dismiss()
                    }.setOnCancelListener {

                    }
                    builder.setCancelable(false).create().show()
                }

                userSelect = false
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {

        }

    }

    internal var submit: View.OnClickListener = View.OnClickListener {
        val workType = spinner_one.selectedItem as String
        var rate = pay_rate.text.toString().trim({ it <= ' ' })
        val task = spinner_Two.selectedItem as String
        if (workType != strings[0] &&
            !TextUtils.isEmpty(rate) &&
            task != taskList[0]
        ) {
            if ((spinner_Two.selectedItem as String).contains(workTypeArray[1]) && task == workTypeArray[1]) {
                Toast.makeText(context, "Insert A product Harvested", Toast.LENGTH_SHORT).show()
            } else {
                rate = String.format("%.2f", java.lang.Float.valueOf(rate))
                val taskObject = TaskObject(workType, Float.valueOf(rate), task)

                val returnIntent = Intent()
                returnIntent.putExtra("TaskObject", taskObject)
                activity?.setResult(Activity.RESULT_OK, returnIntent)
                activity?.finish()
            }

        } else {
            Toast.makeText(context, "Insert All Required data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSpinnerOneSelection(s: String): Int {
        return when (s) {
            "Hourly" -> 1
            "Piece Rate" -> 2
            else -> {
                0
            }
        }
    }

    private fun getSpinnerTwoSelection(s: String): Int {
        val strings = resources.getStringArray(R.array.task_list)
        var i = 1
        for (string in strings) {

            if (s.contains(string)) {
                return i
            }
            i++
        }

        return 0
    }

    private fun getPosition(previous: String?): Int {
        var i = 0
        if (previous != null) {
            for (s in strings) {
                if (previous.contains(s)) {
                    break
                }
                i++

            }
        }

        return i
    }
}
