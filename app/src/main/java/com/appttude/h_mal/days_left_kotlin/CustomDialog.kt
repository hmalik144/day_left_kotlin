package com.appttude.h_mal.days_left_kotlin

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Build
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.date_selector_dialog.*
import kotlinx.android.synthetic.main.date_selector_dialog.view.*
import java.text.DecimalFormat
import java.util.*

class CustomDialog(context: Context): AlertDialog(context){
    companion object{
        lateinit var dateSelectionFrom:String
        lateinit var dateSelectionTo:String
    }

    lateinit var currentTag:String

    init {
        init()
    }

    fun init(){
        setTitle("Select Dates:")
        val dialogView = View.inflate(context, R.layout.date_selector_dialog, null)
        //get date picker
        val datePicker = dialogView.date_picker
        //set onclick listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(dateChangedListener)
        }

        toggleTextButtons(true)

        from_date.setOnClickListener(onClickListener)
        to_date.setOnClickListener(onClickListener)

        val calendar = Calendar.getInstance()
        val dateString = retrieveDateString(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dateSelectionFrom = dateString
        dateSelectionTo = dateString

        create()
    }

    internal var dateChangedListener: DatePicker.OnDateChangedListener =
        DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            var monthOfYear = monthOfYear
            monthOfYear = monthOfYear + 1
            if (currentTag == "from") {
                dateSelectionFrom = retrieveDateString(year, monthOfYear, dayOfMonth)
            } else {
                dateSelectionTo = retrieveDateString(year, monthOfYear, dayOfMonth)
            }
        }

    internal var onClickListener: View.OnClickListener = View.OnClickListener { v ->
        currentTag = v.tag as String

        if (currentTag == "from") {
            toggleTextButtons(true)
            setDateOnDatePicker(dateSelectionFrom, v as DatePicker)

        } else {
            toggleTextButtons(false)
            setDateOnDatePicker(dateSelectionTo,v as DatePicker)
        }
    }

    private fun retrieveDateString(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
        val mFormat = DecimalFormat("00")

        val monthString = mFormat.format(monthOfYear.toLong())
        val dayString = mFormat.format(dayOfMonth.toLong())

        return "$year-$monthString-$dayString"
    }

    private fun setDateOnDatePicker(dateString: String, datePicker: DatePicker) {
        /* 2019-06-04 */
        val year = Integer.parseInt(dateString.substring(0, 4))
        val month = Integer.parseInt(dateString.substring(5, 7)) - 1
        val day = Integer.parseInt(dateString.substring(8))

        datePicker.init(year, month, day, dateChangedListener)

    }

    private fun toggleTextButtons(top: Boolean) {
        setFadeAnimation(from_date)
        setFadeAnimation(to_date)

        if (top) {
            from_date.setTypeface(null, Typeface.BOLD)
            to_date.setTypeface(null, Typeface.NORMAL)

            from_date.setBackgroundColor(context?.getColor(R.color.one)!!)
            to_date.setBackgroundColor(context?.getColor(android.R.color.white)!!)
        } else {
            to_date.setTypeface(null, Typeface.BOLD)
            from_date.setTypeface(null, Typeface.NORMAL)

            to_date.setBackgroundColor(context?.getColor(R.color.one)!!)
            from_date.setBackgroundColor(context?.getColor(android.R.color.white)!!)
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