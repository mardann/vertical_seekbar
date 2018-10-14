package com.onoapps.hanan.verticalseekbar

import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.properties.Delegates.observable
import kotlin.reflect.KProperty

class Label(var view: View, var side: Side = Label.Side.RIGHT) {

    var textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var underlinePaint: Paint = Paint()

    var mTextSize: Int by observable(Utils.spToPx(16f, view.context)) { property: KProperty<*>, oldValue: Int, newValue: Int ->
        textPaint.textSize = newValue.toFloat()

    }

    var labelTextColor: Int by observable(Color.GRAY) { property, oldValue, newValue ->
        textPaint.color = newValue
    }

    var labelFont: Typeface by observable(Typeface.DEFAULT) { property, oldValue, newValue ->
        textPaint.typeface = newValue
    }

    @ColorInt
    var lableUnderlineColor: Int = Color.GRAY
    val mContainingRect = Rect()
    val mTextBounds = Rect()
    var marginFromThumb: Int = Utils.dpToPx(16f, view.context)
    var mIsLabelClickable: Boolean = true

    val mUnderlineMargin = Utils.dpToPx(6f, view.context)
    val mLableMarginRight = Utils.dpToPx(4f, view.context)


    val mLabelPaddingHorizontal = Utils.dpToPx(8f, view.context)

    val mLabelPaddingVertical = Utils.dpToPx(8f, view.context)

    var mLabelText by observable(String()) { property: KProperty<*>, oldValue: String, newValue: String ->
        Log.d("Observable String", "property:$property, oldvalue: $oldValue, new value: $newValue")
        textPaint.getTextBounds(newValue, 0, newValue.length, mTextBounds)

    }

    val mRectPaint: Paint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    fun init() {

        underlinePaint.apply {
            strokeWidth = Utils.dpToPx(1.5f, view.context).toFloat()
            style = Paint.Style.FILL_AND_STROKE
            color = lableUnderlineColor
        }
    }

    private var mLabelIconDrawable: Drawable? = null
    private var mLabelIconBitmap: Bitmap? = null
    private var mLableIconDrawRect: Rect? = null

    fun setIconDrawable(labelIconDrawable: Drawable) {
        mLabelIconDrawable = labelIconDrawable
        mLabelIconBitmap = Bitmap.createBitmap(mLabelIconDrawable!!.intrinsicWidth, mLabelIconDrawable!!.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val labelIcon = Canvas(mLabelIconBitmap!!)
        mLabelIconDrawable!!.setBounds(0, 0, labelIcon.width, labelIcon.height)
        mLabelIconDrawable!!.draw(labelIcon)
        mLableIconDrawRect = mLabelIconDrawable!!.bounds
    }


    fun drawLabel(canvas: Canvas, thumbRect: Rect) {


        mLabelText.let {
            buildLabelRectOnProperSide(thumbRect)

//            canvas.drawRect(mContainingRect, mRectPaint)

            canvas.drawText(
                    mLabelText,
                    (mContainingRect.left + mLabelPaddingHorizontal).toFloat(),
                    (mContainingRect.top + mLabelPaddingVertical + mTextBounds.height()).toFloat(),
                    textPaint
            )

            canvas.drawLine(
                    (mContainingRect.left + mLabelPaddingHorizontal).toFloat(),
                    (mContainingRect.bottom - mLabelPaddingVertical).toFloat(),
                    (mContainingRect.right - mLabelPaddingHorizontal).toFloat(),
                    (mContainingRect.bottom - mLabelPaddingVertical).toFloat(),
                    underlinePaint
            )

            if (mIsLabelClickable) {
                //for y position- we want the icon to be centered with the text. so we calculate half the text height (center) and move up
                //half the icons height
                val iconY = (mContainingRect.top + mTextBounds.height() / 2 + mLabelPaddingHorizontal) - (mLableIconDrawRect!!.height().toFloat() / 2f).toInt()
                mLableIconDrawRect!!.offsetTo(
                        mContainingRect.right - mLabelPaddingHorizontal - mLableIconDrawRect!!.width(),
                        iconY)
                canvas.drawBitmap(mLabelIconBitmap!!, null, mLableIconDrawRect!!, null)
//                canvas.drawRect(mLableIconDrawRect, mRectPaint)
            }
        }
    }

    private fun buildLabelRectOnProperSide(thumbRect: Rect) {
        val rectTop = thumbRect.centerY() - mTextBounds.height() / 2 - mLabelPaddingVertical
        if (side == Side.RIGHT) {

            mContainingRect.set(
                    thumbRect.right + marginFromThumb,
                    rectTop,
                    thumbRect.right + marginFromThumb + totalLabelWidth(),
                    rectTop + totalLabelHeight()
            )
        } else {
            mContainingRect.set(
                    thumbRect.left - marginFromThumb - totalLabelWidth(),
                    rectTop,
                    thumbRect.left - marginFromThumb,
                    rectTop + totalLabelHeight()
            )
        }
    }

    private fun totalLabelWidth(): Int {
        var width = 0
        width += (mTextBounds.width() + mLabelPaddingHorizontal * 2)
        if (mIsLabelClickable) {
            width += (mLableIconDrawRect?.width() ?: 0) + mLableMarginRight
        }
        return width
    }

    private fun totalLabelHeight(): Int {
        return mTextBounds.height() + mUnderlineMargin + mLabelPaddingVertical * 2
    }

    fun isWithinTextBounds(event: MotionEvent): Boolean {
        return mContainingRect.contains(event.x.toInt(), event.y.toInt())
    }

    enum class Side {
        LEFT,
        RIGHT
    }
}