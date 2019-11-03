package com.appttude.h_mal.days_left_kotlin

import android.view.animation.Animation
import android.view.animation.Transformation

class ArcAnimation(val arcView: CircleView, val newAngle : Float) : Animation(){

    var oldAngle: Float = arcView.getArcAngle()

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)
        val angle = 0 + (newAngle - oldAngle) * interpolatedTime

        arcView.setArcAngle(angle)
        arcView.requestLayout()
    }
}