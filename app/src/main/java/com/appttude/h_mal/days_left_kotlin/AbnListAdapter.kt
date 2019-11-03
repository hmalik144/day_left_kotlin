package com.appttude.h_mal.days_left_kotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.appttude.h_mal.days_left_kotlin.Objects.AbnObject
import kotlinx.android.synthetic.main.abn_list_item.view.*

class AbnListAdapter(context: Context, objects: List<AbnObject>) :
    ArrayAdapter<AbnObject>(context, 0, objects){


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.abn_list_item, parent, false)!!
        }

        val currentObject = getItem(position)

        view.farm_name.text = currentObject?.companyName
        view.abn_text.text = currentObject?.abn
        view.postcode_text.text = currentObject?.postCode.toString()

        return view
    }
}