package com.appttude.h_mal.days_left_kotlin

import android.view.animation.Animation
import android.view.animation.Transformation

class BarAnimation(val barView: BarView, val targetWidth: Int, val startWidth : Int) : Animation(){

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)

        val newWidth = (startWidth + targetWidth * interpolatedTime).toInt()

        barView.getLayoutParams().width = newWidth
        barView.requestLayout()
    }
}