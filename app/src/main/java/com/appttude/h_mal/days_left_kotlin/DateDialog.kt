package com.appttude.h_mal.days_left_kotlin

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_add_shift.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class DateDialog(editText: EditText, context: Context) : DatePickerDialog(context) {

    internal var mYear: Int = 0
    internal var mMonth: Int = 0
    internal var mDay: Int = 0

    init {
        val dateString = editText.text.toString()

        var javaDate: Date? = null

        if (dateString.isNotBlank()) {
            val calendar = Calendar.getInstance()
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
        } else {
            try {
                val sdfrmt = SimpleDateFormat("dd/MM/yyyy")
                sdfrmt.isLenient = false
                javaDate = sdfrmt.parse(dateString)
            } catch (e: ParseException) {
                e.printStackTrace()
            } finally {
                if (javaDate != null) {
                    mYear = Integer.parseInt(dateString.substring(6, dateString.length))
                    mMonth = Integer.parseInt(dateString.substring(3, 5)) - 1
                    mDay = Integer.parseInt(dateString.substring(0, 2))
                } else {
                    val calendar = Calendar.getInstance()
                    mYear = calendar.get(Calendar.YEAR)
                    mMonth = calendar.get(Calendar.MONTH)
                    mDay = calendar.get(Calendar.DAY_OF_MONTH)
                }
            }

        }

        Log.i(
            this.javaClass.simpleName, "init: year =" + mYear +
                    "month = " + mMonth +
                    "day = " + mDay
        )


        updateDate(mYear, mMonth, mDay)

        setOnDateSetListener{ view, year, month, dayOfMonth ->
            mYear = year
            mMonth = month + 1
            mDay = dayOfMonth

            val dateString = mYear.toString() + "-" + String.format("%02d", mMonth) + "-" + String.format("%02d", mDay)

            Toast.makeText(context,dateString,Toast.LENGTH_SHORT).show()
            editText.setText(dateString)

        }

        this.setTitle(getContext().getString(R.string.set_date))
        this.show()
    }

}