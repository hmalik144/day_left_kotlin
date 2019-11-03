package com.appttude.h_mal.days_left_kotlin

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.cardview.widget.CardView
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.EMPLOYER_FIREBASE
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.PIECE
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.SHIFT_FIREBASE
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.SHIFT_ID
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.TASK_FIREBASE
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.USER_FIREBASE
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.auth
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.mDatabase
import com.appttude.h_mal.days_left_kotlin.Objects.AbnObject
import com.appttude.h_mal.days_left_kotlin.Objects.ShiftObject
import com.appttude.h_mal.days_left_kotlin.Objects.TaskObject
import com.appttude.h_mal.days_left_kotlin.Objects.TimeObject
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_shift.*
import kotlinx.android.synthetic.main.activity_add_shift.date
import kotlinx.android.synthetic.main.activity_add_shift.search_button
import kotlinx.android.synthetic.main.activity_add_shift.units
import kotlinx.android.synthetic.main.dialog_previous_abns_used.*
import kotlinx.android.synthetic.main.dialog_previous_abns_used.view.*
import kotlinx.android.synthetic.main.fragment_add_task.*
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.list_item.time as time1

class AddShiftActivity : AppCompatActivity() {
    companion object{
        val EMPLOYERREQUEST = 339
        val TASKREQUEST = 445
        val REQUEST = "request"
        val EMPLOYER_CONSTANT = "employer"
        val TASK_CONSTANT = "task"
    }

    var timeObject: TimeObject? = null
    var abnObject: AbnObject? = null
    var taskObject: TaskObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_shift)

        select_employer_card.setOnClickListener(employerListener)
        select_task_card.setOnClickListener(taskListener)
        select_times_card.setOnClickListener{
            val timeDialogClass = TimeDialogClass(this@AddShiftActivity)
            timeDialogClass.create()?.show()
        }

        date.isFocusable = false
        date.setOnClickListener {
            lateinit var dateDialog: DateDialog
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dateDialog = DateDialog(date, this@AddShiftActivity)
                dateDialog.show()
            }

        }

        search_button.setOnClickListener(submitListener)

        intent.getStringExtra(SHIFT_ID)?.let {
            progress_bar.visibility = View.VISIBLE

            mDatabase.child(USER_FIREBASE).child(auth.uid!!).child(SHIFT_FIREBASE).child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        progress_bar.visibility = View.GONE
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        progress_bar.visibility = View.GONE
                        val shiftObject:ShiftObject? = dataSnapshot.getValue(ShiftObject::class.java)

                        shiftObject?.let {shift ->
                            abnObject = shift.abnObject
                            taskObject = shift.taskObject

                            shift.timeObject?.timeIn?.let {
                                timeObject = shift.timeObject
                                setTimeSummary()
                                Toast.makeText(baseContext,"toasted",Toast.LENGTH_SHORT)
                            }

                            if (shift.taskObject?.workType.equals(PIECE)) {
                                units.setText(shift.unitsCount.toString())
                            }

                            date.setText(shiftObject.shiftDate)

                            setTaskCard()
                            setEmployerCard()
                        }
                    }
                })
        }
    }

    internal val submitListener: View.OnClickListener = View.OnClickListener {
        if (timeObject == null){

            Toast.makeText(baseContext,"Time information missing",Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }
        if (abnObject == null){

            Toast.makeText(baseContext,"Employer information missing",Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }
        if (taskObject == null){

            Toast.makeText(baseContext,"Task information missing",Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }
        if (date.text.isBlank()){

            Toast.makeText(baseContext,"Date missing",Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }
        if (taskObject?.workType == PIECE && units.text.isEmpty()){

            Toast.makeText(baseContext,"Units information missing",Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }

        var shiftobj = ShiftObject()

        shiftobj.abnObject = abnObject
        shiftobj.taskObject = taskObject as TaskObject
        shiftobj.timeObject = timeObject as TimeObject
        shiftobj.shiftDate = date.text.toString()
        shiftobj.dateTimeAdded = getDateTimeString()
        shiftobj.unitsCount = units.text.toString().toFloat()

        val shiftReference: DatabaseReference
        val ShiftID = intent.getStringExtra(SHIFT_ID)

        if (ShiftID != null) {
            shiftReference = mDatabase.child(USER_FIREBASE).child(auth.uid!!).child(SHIFT_FIREBASE).child(ShiftID)
            //Updating a shift
        } else {
            shiftReference = mDatabase.child(USER_FIREBASE).child(auth.uid!!).child(SHIFT_FIREBASE).push()
            //Pushing a brand new shift
        }

        progress_bar.visibility = View.VISIBLE
        shiftReference.setValue(shiftobj).addOnCompleteListener{task ->
            Log.i("Firebase", "onComplete: " + task.getResult()!!)
            if (task.isSuccessful()) {
                finish()
            }else{
                Toast.makeText(baseContext,"Could not submit shift", Toast.LENGTH_SHORT).show()
            }
            progress_bar.visibility = View.GONE
        }
    }

    internal var employerListener: View.OnClickListener = View.OnClickListener {
        progress_bar.visibility = (View.VISIBLE)

        turnShiftsIntoRecentlyUsed().let {
            if (it.isNotEmpty()){
                val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_previous_abns_used, null)

                val abnListAdapter = AbnListAdapter(this, it)

                dialogView.list_item_list_dialog.setAdapter(abnListAdapter)

                val builder = AlertDialog.Builder(this)
                builder.setView(dialogView)

                val alertDialog = builder.create()
                alertDialog.show()

                dialogView.button_list_dialog.setOnClickListener(View.OnClickListener {
                    startActivity()
                    alertDialog.dismiss()
                })

                dialogView.list_item_list_dialog.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                    abnObject = it.get(position)
                    setEmployerCard()
                    alertDialog.dismiss()
                })
                progress_bar.setVisibility(View.GONE)
            }else{
                startActivity()
            }
        }
    }

    internal var taskListener: View.OnClickListener = View.OnClickListener {
        progress_bar.visibility = (View.VISIBLE)

        val cont: Context = this

        if (abnObject != null) {
            mDatabase.child(EMPLOYER_FIREBASE).child(abnObject!!.abn!!).child(TASK_FIREBASE)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var list = ArrayList<TaskObject>()
                        p0.exists().let {

                            for (snapshot in p0.children){
                                list.add(snapshot.getValue(TaskObject::class.java)!!)
                            }
                        }

                        val dialogView = LayoutInflater.from(cont).inflate(R.layout.dialog_previous_abns_used, null)

                        val dialogListAdapter = DialogListAdapter(cont, list)
                        dialogView.list_item_list_dialog.setAdapter(dialogListAdapter)

                        val builder = AlertDialog.Builder(cont)
                        builder.setView(dialogView)

                        val alertDialog = builder.create()
                        alertDialog.show()

                        dialogView.button_list_dialog.setOnClickListener(View.OnClickListener {
                            val intent = Intent(cont, AddItemActivity::class.java)
                            intent.putExtra(REQUEST, TASKREQUEST)
                            startActivityForResult(intent, TASKREQUEST)
                            alertDialog.dismiss()
                        })

                        dialogView.list_item_list_dialog.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                            taskObject = list.get(position)
                            setTaskCard()
                            alertDialog.dismiss()
                        })
                        progress_bar.setVisibility(View.GONE)
                    }
                })
        } else {
            val intent = Intent(this@AddShiftActivity, AddItemActivity::class.java)
            intent.putExtra(REQUEST, TASKREQUEST)
            taskObject?.let {
                intent.putExtra(TASK_CONSTANT, it)
            }
            startActivityForResult(intent, TASKREQUEST)
        }
    }

    fun setEmployerCard() {
        if (lable_1.visibility == View.VISIBLE) {
            toggleViewVisibility(select_employer_card)
        }

        employer_name.text = abnObject?.companyName
        val loc = abnObject?.state + " " + abnObject?.postCode
        employer_location.text = loc
    }

    fun setTaskCard() {
        if (lable_2.getVisibility() == View.VISIBLE) {
            toggleViewVisibility(select_task_card)
        }

        task.text = taskObject?.task

        var summary = taskObject?.workType + " - $" + taskObject?.rate + " /"
        if (taskObject?.workType.equals("Piece Rate")) {
            summary = summary + "Unit"

            units.visibility = View.VISIBLE
            select_times_card.visibility = (View.VISIBLE)
        } else {
            summary = summary + "Hour"

            units.visibility = View.GONE
            select_times_card.visibility = (View.VISIBLE)
        }

        task_summary.setText(summary)
    }

    private fun toggleViewVisibility(cardView: CardView) {
        when (cardView.getId()) {
            R.id.select_employer_card -> {
                setVisibility(employer_layout)
                setVisibility(lable_1)
            }
            R.id.select_task_card -> {
                setVisibility(task_layout)
                setVisibility(lable_2)
            }
            R.id.select_times_card -> {
                setVisibility(time_layout)
                setVisibility(lable_3)
            }
        }

    }

    private fun setVisibility (view: View) {
        val vis: Int
        if (view.visibility == View.VISIBLE) {
            vis = View.GONE
        } else {
            vis = View.VISIBLE
        }

        view.visibility = vis
    }

    private fun turnShiftsIntoRecentlyUsed() : List<AbnObject>{
        val uniqueList = mutableMapOf<String,AbnObject>()
        MainActivity.shiftList.forEach {
            uniqueList.put(it.abnObject?.abn!!, it.abnObject!!)
        }

        return uniqueList.values.toList()
    }

    private fun startActivity() {
        val intent = Intent(this, AddItemActivity::class.java)
        intent.putExtra(REQUEST, EMPLOYERREQUEST)
        startActivityForResult(intent, EMPLOYERREQUEST)
    }

    internal inner class TimeDialogClass(context: Context?) :
        AlertDialog.Builder(context) {

        private var timePickerTimePicker: TimePicker? = null
        private var startTimeTextView: TextView? = null
        private var finishTimeTextView: TextView? = null
        private var breakEditText: EditText? = null

        private var currentTag: String? = null
        private var alertDialog: AlertDialog? = null
        private var breakInt: Int = 0


        var timeSelect: View.OnClickListener = View.OnClickListener { v ->

            currentTag = v.tag as String
            val timeString: String?

            if (currentTag == "start") {
                timeString = timeObject?.timeIn
                toggleTextButtons(true)

            } else {
                timeString = timeObject?.timeOut
                toggleTextButtons(false)
            }

            if (timeString != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePickerTimePicker!!.hour = getTime(timeString)[0]
                    timePickerTimePicker!!.minute = getTime(timeString)[1]
                }
            } else {
                val calendar = Calendar.getInstance()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePickerTimePicker!!.hour = calendar.get(Calendar.HOUR_OF_DAY)
                    timePickerTimePicker!!.minute = calendar.get(Calendar.MINUTE)
                }

            }
        }

        init {
            if (timeObject?.timeIn == null && timeObject?.timeOut == null){
                timeObject = TimeObject()
            }

            setView(null)
        }

        override fun setView(view: View?): AlertDialog.Builder {
            val view = View.inflate(context, R.layout.dialog_add_times, null)

            currentTag = "start"

            startTimeTextView = view!!.findViewById(R.id.from_date)
            finishTimeTextView = view.findViewById(R.id.to_date)
            timePickerTimePicker = view.findViewById(R.id.time_picker)
            breakEditText = view.findViewById(R.id.breaktime)
            val okText: TextView = view.findViewById(R.id.ok)

            timePickerTimePicker!!.setIs24HourView(true)

            initialiseTime()

            okText.setOnClickListener {
                if (timeObject?.timeIn != null && timeObject?.timeOut != null) {
                    timeObject?.hours = calculateDuration()
                    timeObject?.breakEpoch = breakInt

                    Toast.makeText(context, convertTimeFloat(timeObject?.hours!!), Toast.LENGTH_SHORT).show()

                    if (lable_3.visibility == View.VISIBLE) {
                        toggleViewVisibility(select_times_card)
                    }

                    time.text = convertTimeFloat(timeObject?.hours!!)

                    setTimeSummary()
                }

                alertDialog!!.dismiss()
            }

            breakEditText!!.setText(timeObject?.breakEpoch.toString())

            startTimeTextView!!.tag = "start"
            finishTimeTextView!!.tag = "finish"

            startTimeTextView!!.setOnClickListener(timeSelect)
            finishTimeTextView!!.setOnClickListener(timeSelect)

            timePickerTimePicker!!.setOnTimeChangedListener{view, hourOfDay, minute ->
                val ddTime = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute)

                if (currentTag == "start") {
                    timeObject!!.timeIn = ddTime
                } else {
                    timeObject!!.timeOut = ddTime
                }
            }
            toggleTextButtons(true)

            return super.setView(view)
        }

        override fun create(): AlertDialog? {
            alertDialog = super.create()

            return alertDialog
        }

        override fun setNegativeButton(textId: Int, listener: DialogInterface.OnClickListener): AlertDialog.Builder {
            return super.setNegativeButton(
                android.R.string.cancel
            ) { dialog, which -> dialog.dismiss() }
        }

        private fun getTime(s: String): IntArray {

            return intArrayOf(Integer.parseInt(s.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]),
                Integer.parseInt(s.split(
                    ":".toRegex()
                ).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                )
            )
        }

        private fun initialiseTime(){
            val timeString = timeObject?.timeIn

            if (timeString != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePickerTimePicker!!.hour = getTime(timeString)[0]
                    timePickerTimePicker!!.minute = getTime(timeString)[1]
                }
            } else {
                val calendar = Calendar.getInstance()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePickerTimePicker!!.hour = calendar.get(Calendar.HOUR_OF_DAY)
                    timePickerTimePicker!!.minute = calendar.get(Calendar.MINUTE)
                }

            }
        }

        private fun calculateDuration(): Float {
            val startTime = timeObject?.timeIn
            val finishTime = timeObject?.timeOut
            val breakText = breakEditText!!.text.toString().trim { it <= ' ' }

            val hoursIn = getTime(startTime!!)[0]
            val hoursOut = getTime(finishTime!!)[0]
            val minutesIn = getTime(startTime)[1]
            val minutesOut = getTime(finishTime)[1]

            breakInt = 0
            if (!TextUtils.isEmpty(breakText)) {
                breakInt = Integer.parseInt(breakText)
            }
            val duration: Float

            if (hoursOut > hoursIn) {
                duration =
                    hoursOut.toFloat() + minutesOut.toFloat() / 60 - (hoursIn.toFloat() + minutesIn.toFloat() / 60) - breakInt.toFloat() / 60
            } else {
                duration =
                    hoursOut.toFloat() + minutesOut.toFloat() / 60 - (hoursIn.toFloat() + minutesIn.toFloat() / 60) - breakInt.toFloat() / 60 + 24
            }

            val s = String.format("%.2f", duration)
            return java.lang.Float.parseFloat(s)
        }

        private fun toggleTextButtons(top: Boolean) {
            setFadeAnimation(startTimeTextView!!)
            setFadeAnimation(finishTimeTextView!!)

            if (top) {
                startTimeTextView!!.setTypeface(null, Typeface.BOLD)
                finishTimeTextView!!.setTypeface(null, Typeface.NORMAL)

                startTimeTextView!!.setBackgroundColor(context.resources.getColor(R.color.one))
                finishTimeTextView!!.setBackgroundColor(context.resources.getColor(android.R.color.white))
            } else {
                finishTimeTextView!!.setTypeface(null, Typeface.BOLD)
                startTimeTextView!!.setTypeface(null, Typeface.NORMAL)

                finishTimeTextView!!.setBackgroundColor(context.resources.getColor(R.color.one))
                startTimeTextView!!.setBackgroundColor(context.resources.getColor(android.R.color.white))
            }
        }

        private fun setFadeAnimation(view: View) {
            val bottomUp = AnimationUtils.loadAnimation(
                context,
                R.anim.fade_in
            )

            view.animation = bottomUp
        }


    }

    private fun convertTimeFloat(timeIn: Float): String {
        val hour = timeIn.toInt()
        val minutes = (timeIn - hour).toInt() * 60

        return hour.toString() + "hours " + minutes.toString() + "mins"
    }

    private fun setTimeSummary() {
        if (lable_3.visibility == View.VISIBLE) {
            toggleViewVisibility(select_times_card)
        }

        val s = timeObject?.timeIn + " - " + timeObject?.timeOut
        time_summary.text = s
        if (timeObject!!.breakEpoch > 0) {
            break_holder.visibility = View.VISIBLE
            break_summary.text = timeObject!!.breakEpoch.toString() +  " minutes"
        } else {
            break_holder.visibility = View.GONE
        }

        time.setText(convertTimeFloat(timeObject!!.hours))
    }

    fun getDateTimeString(): String {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.ENGLISH)

        return sdf.format(cal.time)
    }
}
