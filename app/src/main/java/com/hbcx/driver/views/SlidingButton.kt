package com.hbcx.driver.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.hbcx.driver.R
import cn.sinata.xldutils.sysErr
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.dip


/**
 * 滑动按钮
 * Created by zhangyujiu on 2018/1/2.
 */
class SlidingButton : View {
    lateinit var gradientDrawable: GradientDrawable
    lateinit var paint: Paint
    lateinit var mBound: Rect
    var mText = "开始行程"
    var flagText = mText
    lateinit var bmp: Bitmap

    var onSwipeListener: (() -> Unit)? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        val colors = intArrayOf(ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.colorPrimary))
        gradientDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors)
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        backgroundDrawable = gradientDrawable

        mBound = Rect()

        paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.textSize = dip(17).toFloat()
        paint.color = ContextCompat.getColor(context, R.color.white)

        bmp = BitmapFactory.decodeResource(context.resources, R.mipmap.ico_arrow_right_double)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = onMeasureR(0, widthMeasureSpec)
        val height = onMeasureR(1, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    /**
     * 计算控件宽高
     *
     * @param attr属性
     * [0宽,1高]
     * @param oldMeasure
     */
    fun onMeasureR(attr: Int, oldMeasure: Int): Int {

        var newSize = 0
        val mode = View.MeasureSpec.getMode(oldMeasure)
        val oldSize = View.MeasureSpec.getSize(oldMeasure)

        when (mode) {
            View.MeasureSpec.EXACTLY -> newSize = oldSize
            View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED -> {

                val value: Float

                if (attr == 0) {

                    value = mBound.width().toFloat()
                    // value = mPaint.measureText(mText);

                    // 控件的宽度  + getPaddingLeft() +  getPaddingRight()
                    newSize = (paddingLeft.toFloat() + value + paddingRight.toFloat()).toInt()

                } else if (attr == 1) {

                    value = mBound.height().toFloat()

                    // 控件的高度  + getPaddingTop() +  getPaddingBottom()
                    newSize = (paddingTop.toFloat() + value + paddingBottom.toFloat()).toInt()

                }
            }
        }

        return newSize
    }

    var moveStartX = 0f
    var moveEndX = 0f
    var dx = 0f
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //重新测量文字
        paint.getTextBounds(mText, 0, mText.length, mBound)
        val fm = paint.fontMetricsInt
        /*
         * 控件宽度/2 - 文字宽度/2
         */
        val startX = width / 2 - mBound.width() / 2

        /*
         * 控件高度/2 + 文字高度/2,绘制文字从文字左下角开始,因此"+"
         */
        val startY = height / 2 - fm.descent + (fm.bottom - fm.top) / 2

        // 绘制文字
        canvas?.drawText(mText, startX.toFloat(), startY.toFloat(), paint)
        val bmpHeight = bmp.height
        canvas?.drawBitmap(bmp, bmp.width.toFloat() + dx, (height / 2 - bmpHeight / 2).toFloat(), paint)
        if (bmp.isRecycled) {
            bmp.recycle()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                moveStartX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                moveEndX = event.x
                dx = moveEndX - moveStartX
                if (dx <= 0) {
                    dx = 0f
                }
                mText = if (dx >= width / 2) {
                    "松开触发"
                } else {
                    flagText
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                if (dx >= width / 2) {
                    sysErr("松开触发")
                    onSwipeListener?.invoke()
                }
                dx = 0f
                mText=flagText
                invalidate()
            }
        }

        return true
    }

    //更新按钮文字
    fun changeButtonText(content: String) {
        if (!TextUtils.isEmpty(content)) {
            mText = content
            flagText = mText
            invalidate()
        }
    }
}