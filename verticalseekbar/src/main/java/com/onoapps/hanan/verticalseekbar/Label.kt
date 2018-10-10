package com.onoapps.hanan.verticalseekbar

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.view.View
import com.onoapps.hanan.verticalseekbar.R.drawable.thumb
import kotlin.properties.Delegates.observable
import kotlin.reflect.KProperty

class Label(view: View) {

    val mContainingRect = Rect()
    val mTextBounds = Rect()
    var marginFromThumb: Int = Utils.dpToPx(4f, view.context)
    val mLabelIconPadding = Utils.dpToPx(4f, view.context)
    var mIsLabelClickable: Boolean = true

    val textPaint = Paint().apply { }

    var mLabelText by observable(String()) { property: KProperty<*>, oldValue: String, newValue: String ->

    }



    private fun drawLabel(canvas: Canvas, guidePoint: Point) {

        if (mLabelText != null) {

            canvas.drawText(mLabelText!!,
                    (guidePoint.x + marginFromThumb).toFloat(),
                    (guidePoint.y - mTextBounds.centerY()).toFloat(),
                    textPaint)

            var underlineEnd = guidePoint.x + marginFromThumb + mTextBounds.right

            if (mIsLabelClickable) {
                val rightOfTextWithPadding = guidePoint.x + marginFromThumb + mTextBounds.right + mLabelIconPadding
                mLableIconDrawRect!!.offsetTo(rightOfTextWithPadding, guidePoint.y - mLableIconDrawRect!!.height() / 2)
                canvas.drawBitmap(mLabelIconBitmap!!, null, mLableIconDrawRect!!, null)
                //extend line under icon too
                underlineEnd += mLableIconDrawRect!!.width()
            }

            val underlieHeight = guidePoint.y - mTextBounds.centerY() + mUnderlineMargin
            canvas.drawLine((guidePoint.x + mLableMarginRight).toFloat(),
                    underlieHeight.toFloat(),
                    underlineEnd.toFloat(),
                    underlieHeight.toFloat(),
                    mUnderlinePaint!!)
        }
    }
}