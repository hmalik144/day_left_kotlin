package com.appttude.h_mal.days_left_kotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class BarView(context: Context?, attrs: AttributeSet?) : View(context, attrs){

    private var topRect: RectF = RectF()
    private var bottomRect: RectF = RectF()
    private var hardEdge: RectF = RectF()

    private var colourOne: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var colourTwo: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var cover:Float

    init {
        colourOne.color = Color.BLUE
        colourTwo.color = Color.GREEN
        cover = 0.toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        topRect.centerX()
        topRect.centerY()
        topRect.left = 0f
        topRect.top = 0f
        topRect.right = width.toFloat()
        topRect.bottom = height.toFloat()

        canvas?.drawRoundRect(topRect, 20f, 20f, colourOne)

        bottomRect.centerX()
        bottomRect.centerY()
        if (cover < 0.05f) {
            cover = 0.05f
        }
        bottomRect.left = (width - 20) * cover
        bottomRect.top = 0f
        bottomRect.right = width.toFloat()
        bottomRect.bottom = height.toFloat()

        canvas?.drawRoundRect(bottomRect, 20f, 20f, colourTwo)

        hardEdge.centerX()
        hardEdge.centerY()
        hardEdge.top = bottomRect.top
        hardEdge.bottom = bottomRect.bottom

        hardEdge.left = bottomRect.left
        hardEdge.right = hardEdge.left + 60

        canvas?.drawRoundRect(hardEdge, 0f, 0f, colourTwo)
    }

    fun getCover(): Float {
        return cover
    }

    fun setCover(cover: Float) {
        this.cover = cover
    }

    fun setCover(total: Float, number: Float) {
        this.cover = number / total
    }

    fun setColourOne(colour: Int) {
        colourOne.color = colour
    }

    fun setColourTwo(colour: Int) {
        colourTwo.color = colour
    }
}