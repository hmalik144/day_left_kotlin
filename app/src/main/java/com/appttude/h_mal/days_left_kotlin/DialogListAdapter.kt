package com.appttude.h_mal.days_left_kotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.appttude.h_mal.days_left_kotlin.Objects.TaskObject
import kotlinx.android.synthetic.main.task_list_item.*
import kotlinx.android.synthetic.main.task_list_item.view.*

class DialogListAdapter(context: Context, objects: MutableList<TaskObject>) :
    ArrayAdapter<TaskObject>(context, 0, objects){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.task_list_item, parent, false)!!
        }

        val currentTask = getItem(position)

        view.task.text = currentTask?.task
        var summary = currentTask?.workType + " - $" + currentTask?.rate + " /"

        if (currentTask?.workType.equals("Piece Rate")) {
            summary = "$summary Unit"
            view.task_image.setImageResource(R.drawable.piece)
        } else if(currentTask?.workType.equals("Hourly")){
            summary = "$summary Hour"
            view.task_image.setImageResource(R.drawable.task)
        }

        view.task_summary.setText(summary)

        return view
    }
}