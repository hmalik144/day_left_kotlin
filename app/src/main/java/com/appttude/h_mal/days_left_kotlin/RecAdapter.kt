package com.appttude.h_mal.days_left_kotlin

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appttude.h_mal.days_left_kotlin.Objects.ShiftObject
import java.util.HashSet

class RecAdapter(val context: Context, val shiftList : List<ShiftObject>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var uniqueEntries: Int = 0
    var typeCount: IntArray

    init {
        uniqueEntries = countDistinct()
        typeCount = countShiftType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> {
                val itemOne = LayoutInflater.from(context).inflate(R.layout.item_one, parent, false)
                return ItemOne(itemOne)
            }
            2 -> {
                val itemTwo = LayoutInflater.from(context).inflate(R.layout.item_two, parent, false)
                return ItemTwo(itemTwo)
            }
            3 -> {
                val itemThree =
                    LayoutInflater.from(context).inflate(R.layout.item_three, parent, false)
                return ItemThree(itemThree)
            }
            else -> {
                val itemThree =
                    LayoutInflater.from(context).inflate(R.layout.item_three, parent, false)
                return ItemThree(itemThree)
            }
        }


    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                val viewHolderCurrent = holder as ItemOne

                val arcView = viewHolderCurrent.arc
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    arcView.setPaintColor(context.getColor(R.color.two))
                }

                val days = uniqueEntries

                val complete = 360 * days / 88

                viewHolderCurrent.days.text = days.toString()

                val animation = ArcAnimation(arcView, complete.toFloat())
                animation.setDuration(600)
                arcView.startAnimation(animation)
            }
            2 -> {
                val viewTwo = holder as ItemTwo

                val barView = viewTwo.barView
                val linearLayout = viewTwo.linearLayout

                val cover = typeCount[1].toFloat() / shiftList.size

                barView.setCover(cover)

                barView.setColourOne(context.getColor(R.color.four))
                barView.setColourTwo(context.getColor(R.color.three))

                viewTwo.pcText.text = typeCount[1].toString()
                viewTwo.hrText.text = typeCount[0].toString()

                //                viewTwo.textholder.setPadding(60,0,60,0);
                //                viewTwo.bottomTextholder.setPadding(60,0,60,0);

                linearLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {

                        val barAnimation = BarAnimation(barView, linearLayout.width, 0)
                        barAnimation.setDuration(600)
                        barView.setAnimation(barAnimation)
                        linearLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }

            3 -> {
                val viewCounting = holder as ItemThree

                val cardTitle = viewCounting.cardTitle
                val cardIcon = viewCounting.cardIcon
                val units = viewCounting.units
                val totalEarned = viewCounting.totalEarned
                val top = viewCounting.textholderTop
                val bottom = viewCounting.textholderBottom

                //                top.setPadding(60,0,60,0);
                //                bottom.setPadding(60,0,60,0);
                //                cardIcon.setPadding(0,0,10,0);

                if (position == 2) {
                    cardTitle.text = "Hourly"
                    cardIcon.setImageResource(R.drawable.clock_icon)
                    val hours = String.format("%.2f", calculateHours())
                    units.text = "$hours Hours"

                    val total = String.format("%.2f", calculateAccumulatedPay(0))
                    totalEarned.text = "$$total"
                }
                if (position == 3) {
                    cardTitle.text = "Piece"
                    cardTitle.setTextColor(context.resources.getColor(R.color.three))
                    cardIcon.setImageResource(R.drawable.piece)
                    cardIcon.rotation = 270f
                    val pieces = String.format("%.2f", calculateUnits())
                    units.text = "$pieces Units"
                    val total = String.format("%.2f", calculateAccumulatedPay(1))
                    totalEarned.text = "$$total"
                }
                if (position == 4) {
                    cardTitle.visibility = View.GONE
                    cardIcon.visibility = View.GONE

                    val total = String.format("%.2f", calculateAccumulatedPay(3))
                    totalEarned.text = "$$total"

                    viewCounting.textholderTop.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (position) {
            0 -> return 1
            1 -> return 2
            2 -> return 3
            3 -> return 3
            4 -> return 3
            else -> {
                return 0
            }
        }
    }

    internal inner class ItemOne(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var arc: CircleView
        var days: TextView

        init {
            arc = itemView.findViewById<View>(R.id.arc_view) as CircleView
            days = itemView.findViewById(R.id.days_completed)
        }
    }

    internal inner class ItemTwo (itemView: View) : RecyclerView.ViewHolder(itemView) {

        var barView: BarView
        var linearLayout: LinearLayout
        var pcText: TextView
        var hrText: TextView

        init {
            barView = itemView.findViewById(R.id.bar) as BarView
            linearLayout = itemView.findViewById(R.id.lin)
            pcText = itemView.findViewById(R.id.pc_amount_text)
            hrText = itemView.findViewById(R.id.hr_amount_text)
        }
    }

    internal inner class ItemThree(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardTitle: TextView
        val cardIcon: ImageView
        val units: TextView
        val totalEarned: TextView
        val textholderTop: LinearLayout
        val textholderBottom: LinearLayout

        init {
            cardTitle = itemView.findViewById(R.id.card_title)
            cardIcon = itemView.findViewById(R.id.card_icon)
            units = itemView.findViewById(R.id.units)
            totalEarned = itemView.findViewById(R.id.total_earned)
            textholderTop = itemView.findViewById(R.id.text_holder)
            textholderBottom = itemView.findViewById(R.id.text_holder_two)
        }
    }

    private fun calculateHours(): Float {
        var hours = 0f
        for (shiftObject in shiftList) {
            if (shiftObject.taskObject?.workType.equals("Hourly")) {
                hours = hours + shiftObject.timeObject!!.hours - shiftObject.timeObject!!.breakEpoch
            }
        }

        return hours
    }

    private fun calculateUnits(): Float {
        var units = 0f
        for (shiftObject in shiftList) {
            if (shiftObject.taskObject?.workType.equals("Piece Rate")) {
                units += + shiftObject.unitsCount!!
            }
        }

        return units
    }

    private fun calculateAccumulatedPay(type: Int): Float {
        var pay = 0f

        for (shiftObject in shiftList) {
            when (type){
                0 -> {
                    if (shiftObject.taskObject?.workType == "Hourly") {
                        pay += shiftObject.taskObject?.rate?.times((shiftObject.timeObject!!.hours - shiftObject.timeObject!!.breakEpoch))
                            ?: pay
                    }
                }
                1 -> {
                    if (shiftObject.taskObject?.workType == "Piece Rate") {
                        pay += shiftObject.taskObject?.rate?.times(shiftObject.unitsCount!!) ?: pay
                    }
                }
                else -> {
                    if (shiftObject.taskObject?.workType == "Hourly") {
                        pay += shiftObject.taskObject?.rate?.times((shiftObject.timeObject!!.hours - shiftObject.timeObject!!.breakEpoch))
                            ?: pay
                    } else {
                        pay += shiftObject.taskObject?.rate?.times(shiftObject.unitsCount!!) ?: pay
                    }
                }
            }

        }

        return pay
    }

    private fun countDistinct(): Int {
        val hs = HashSet<String>()

        for (i in shiftList.indices) {

            hs.add(shiftList.get(i).shiftDate)
        }

        return hs.size
    }

    private fun countShiftType(): IntArray {
        var i = 0
        var j = 0

        for (shiftObject in shiftList) {
            if (shiftObject.taskObject?.workType.equals("Hourly")) {
                i++
            } else {
                j++
            }
        }

        return intArrayOf(i, j)
    }
}