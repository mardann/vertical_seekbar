package com.onoapps.hanan.verticalseekbar

import android.graphics.*
import android.graphics.drawable.Drawable
import android.service.quicksettings.TileService
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.concurrent.ThreadPoolExecutor
import kotlin.math.log
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable

class Thumb(view: View, val verticalPadding: Int = 0, val horizontalPadding: Int = 0) {

    var mThumbDrawable: Drawable? = null
    lateinit var mImgThumb: Bitmap
    val mContainingRect: Rect = Rect()
    var mThumbHalfHeight: Int = 0
    var mThumbHalfWidth: Int = 0
    private val thumbCenter: Point = Point()

    val mThumbPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        setShadowLayer(12f, -3f, 3f, -0x70000000)
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, this)
    }

    val mRectPaint: Paint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    fun init() {
        mThumbHalfWidth = mThumbDrawable!!.intrinsicWidth / 2
        mThumbHalfHeight = mThumbDrawable!!.intrinsicHeight / 2
        mImgThumb = Bitmap.createBitmap(mThumbDrawable!!.intrinsicWidth, mThumbDrawable!!.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val thumbCanvas = Canvas(mImgThumb)
        mThumbDrawable!!.setBounds(0, 0, thumbCanvas.width, thumbCanvas.height)
        mThumbDrawable!!.draw(thumbCanvas)
    }


    fun drawThumb(canvas: Canvas) {
        canvas.drawBitmap(mImgThumb, (thumbCenter.x - mThumbHalfWidth).toFloat(), (thumbCenter.y - mThumbHalfHeight).toFloat(), mThumbPaint)
//        canvas.drawRect(mContainingRect, mRectPaint)
    }

    fun isWithinThumbBounds(event: MotionEvent): Boolean {
        return mContainingRect.contains(event.x.toInt(), event.y.toInt())
    }

    fun updateCenterPoint(x: Int? = null, y: Int){
        thumbCenter.x = x?:thumbCenter.x
        thumbCenter.y = y
        mContainingRect.set(
                thumbCenter.x - mThumbHalfWidth - horizontalPadding,
                thumbCenter.y - mThumbHalfHeight - verticalPadding,
                thumbCenter.x + mThumbHalfWidth + horizontalPadding,
                thumbCenter.y + mThumbHalfHeight + verticalPadding)

    }

    fun getCenterPoint():Point{
        return thumbCenter
    }
}