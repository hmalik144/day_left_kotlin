package com.appttude.h_mal.days_left_kotlin

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View

class CircleView(context: Context?, attrs: AttributeSet?) : View(context, attrs){

    private var arcAngle: Float = 0.toFloat()
    private var mRect: RectF = RectF()
    private var mRect2: RectF = RectF()
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paint2: Paint = Paint()
    private var thinkness: Int = 0

    init {
        paint2.isAntiAlias = true
        paint2.color = Color.TRANSPARENT
        paint2.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (thinkness == 0) {
            thinkness = 35
        }


        mRect.centerX()
        mRect.centerY()
        mRect.left = 10f
        mRect.top = 10f
        mRect.right = (width - 10).toFloat()
        mRect.bottom = (width - 10).toFloat()

        mRect2.centerX()
        mRect2.centerY()
        mRect2.left = mRect.left + thinkness
        mRect2.top = mRect.top + thinkness
        mRect2.right = mRect.right - thinkness
        mRect2.bottom = mRect.bottom - thinkness

        setBackgroundColor(Color.TRANSPARENT)
        if (mPaint.getColor() == 0) {
            mPaint.setColor(Color.BLUE)
        }

        setLayerType(LAYER_TYPE_SOFTWARE, null)

        canvas?.drawArc(mRect, 270f, arcAngle, true, mPaint)
        canvas?.drawOval(mRect2, paint2)
    }

    fun setPaintColor(color: Int) {
        mPaint.setColor(color)
    }

    fun setThickness(thickness: Int) {
        this.thinkness = thickness
    }

    fun getArcAngle(): Float {
        return arcAngle
    }

    fun setArcAngle(arcAngle: Float) {
        this.arcAngle = arcAngle
    }
}