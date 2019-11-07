package com.appttude.h_mal.days_left_kotlin

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import com.appttude.h_mal.days_left_kotlin.CustomDialog.Companion.dateSelectionFrom
import com.appttude.h_mal.days_left_kotlin.CustomDialog.Companion.dateSelectionTo
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.SHIFT_FIREBASE
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.SHIFT_ID
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.USER_FIREBASE
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.auth
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.mDatabase
import com.appttude.h_mal.days_left_kotlin.MainActivity.Companion.ref
import com.appttude.h_mal.days_left_kotlin.Objects.AbnObject
import com.appttude.h_mal.days_left_kotlin.Objects.ShiftObject
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import kotlinx.android.synthetic.main.date_selector_dialog.*
import kotlinx.android.synthetic.main.date_selector_dialog.view.*
import kotlinx.android.synthetic.main.dialog_previous_abns_used.*
import kotlinx.android.synthetic.main.dialog_previous_abns_used.view.*
import kotlinx.android.synthetic.main.dialog_search_employer.*
import kotlinx.android.synthetic.main.fragment_list.*
import java.lang.ref.PhantomReference
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentList : androidx.fragment.app.Fragment() {

    lateinit var fireAdapter:FireAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //set custom firebase adapter on listview
        fireAdapter = FireAdapter(activity, ShiftObject::class.java,R.layout.list_item,ref)
        page_two_list.adapter = fireAdapter

        page_two_list.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val refId = fireAdapter.getId(position)
                val intent = Intent(activity, AddShiftActivity::class.java)
                intent.putExtra(SHIFT_ID, refId)
                startActivity(intent)
            }
        })

        page_two_list.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener{
            override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Are you sure you want to delete?")
                builder.setNegativeButton(android.R.string.no, null)
                builder.setPositiveButton(
                    android.R.string.yes
                ) { dialog, which ->

                    fireAdapter.getRef(position).removeValue()
                }
                builder.create().show()

                return false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_list_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.getItemId()) {
            R.id.app_bar_filter -> {
                filterData()
                return false
            }
            R.id.app_bar_soft -> {
                sortData()
                return false
            }
        }
        return false
    }

    private fun sortData() {
        val grpname = arrayOf("Name", "Date Added", "Date of shift")
        val checkedItem = -1

        val alt_bld = AlertDialog.Builder(context)
        alt_bld.setTitle("Sort by:")
        alt_bld.setSingleChoiceItems(grpname, checkedItem) { dialog, item ->
            when (item) {
                0 -> {
                    val q1 = ref.orderByChild("abnObject/companyName").equalTo("GREEN CLOUD NURSERY")
                    fireAdapter = FireAdapter(activity, ShiftObject::class.java, R.layout.list_item, q1)
                }
                1 -> fireAdapter = FireAdapter(
                    activity,
                    ShiftObject::class.java,
                    R.layout.list_item,
                    ref.orderByChild("dateTimeAdded")
                )
                2 -> fireAdapter = FireAdapter(
                    activity,
                    ShiftObject::class.java,
                    R.layout.list_item,
                    ref.orderByChild("shiftDate")
                )
            }
            page_two_list.adapter = fireAdapter
            dialog.dismiss()
        }
        alt_bld.create().show()
    }

    private fun filterData(){
        val groupName = arrayOf("Name", "Date Added", "Shift Type")
        val checkedItem = -1

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Filter by:")
        builder.setSingleChoiceItems(groupName,checkedItem, DialogInterface.OnClickListener{dialog, item ->
            dialog.dismiss()

            when(item) {
                0 -> {
                    val dialogBuilder = AlertDialog.Builder(context)
                    dialogBuilder.setTitle("Select Employer:")
                    //get layout
                    val dialogView = View.inflate(context, R.layout.dialog_previous_abns_used, null)
                    //hide button
                    dialogView.button_list_dialog.visibility = View.GONE
                    //get listview
                    val listView = dialogView.list_item_list_dialog
                    //get unique abn objects
                    val uniqueAbnObjects= turnToUniqueAbnObject(MainActivity.shiftList)
                    //populate list in view
                    listView.adapter = AbnListAdapter(context!!,uniqueAbnObjects as MutableList<AbnObject>)
                    //on item click listener
                    listView.setOnItemClickListener(AdapterView.OnItemClickListener{parent, view, position, id ->
                        applyFilter(uniqueAbnObjects[position].abn!!,null)
                    })
                    //set view on dialog
                    dialogBuilder.setView(dialogView)

                    dialogBuilder.create().show()

                }
                1 -> {
                    val customDialog = CustomDialog(context!!)

                    customDialog.setButton(BUTTON_POSITIVE, getContext()?.getString(android.R.string.yes),
                        DialogInterface.OnClickListener{ dialogNew, which ->
                            //interface results back
                            if (dateSelectionFrom != dateSelectionTo) {
                                applyFilter(dateSelectionFrom, dateSelectionTo)
                            }

                            customDialog.dismiss()
                        })

                    customDialog.create()
                }
                2 -> {
                    val typeDialog = AlertDialog.Builder(context)
                    val typeString = arrayOf("Hourly", "Piece Rate")

                    typeDialog.setSingleChoiceItems(
                        arrayOf("Hourly", "Piece Rate"), -1
                    ) { dialog, which ->
                        val q1 = ref.orderByChild("taskObject/workType").equalTo(typeString[which])

                        fireAdapter = FireAdapter(activity, ShiftObject::class.java, R.layout.list_item, q1)
                        page_two_list.adapter = fireAdapter
                    }
                    typeDialog.create().show()
                }
            }
        })
    }

    fun turnToUniqueAbnObject(shifts : ArrayList<ShiftObject>): List<AbnObject>{
        val abnList = mutableListOf<AbnObject>()

        shifts.forEach{shiftObject ->
            shiftObject.abnObject?.let { abnList.add(it) }
        }

        return abnList.distinct()
    }

    fun applyFilter(arg1: String, arg2: String?) {
        val q1: Query
        if (arg2 == null) {
            q1 = ref.orderByChild("abnObject/abn").equalTo(arg1)
        } else {
            q1 = ref.orderByChild("shiftDate").startAt(arg1).endAt(arg2)
        }

        fireAdapter = FireAdapter(activity, ShiftObject::class.java, R.layout.list_item, q1)
        page_two_list.adapter = fireAdapter
    }


}
