package com.onoapps.hanan.verticalseekbar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcelable
import android.support.annotation.DrawableRes
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

/**
 * @author hanandann
 */

class VerticalSeekBar : View {

    private val TAG = this.javaClass.simpleName

    private var mLableUnderlineColor: Int = 0
    private var mLabelTextColor: Int = 0
    private var mProgFullColor: Int = 0
    private var mProgEmptyColor: Int = 0
    private var mPercentFull: Float = 0.toFloat()
    internal var mMaxValue: Int = 0
    internal var mMinValue: Int = 0
    internal var mUpdateStep: Int = 0
    private var mLabelFont: Typeface? = null
    private var mLabelIconDrawable: Drawable? = null
    private lateinit var mThumbDrawable: Drawable
    private var mTextSize: Int = 0


    internal var centerHorizontal: Int = 0

    internal var initialVertical: Float = 0.toFloat()

    internal var isDragging = false


    private lateinit var thumb: Thumb


    private var mScrollBarHeight: Int = 0

    internal var progressBarRadius = Utils.dpToPx(4f, context)

    private val mProgBar: RectF = RectF()
    private var mEmptyProgBarPaint: Paint? = null
    private val mFullProgBarRect: RectF = RectF()
    private var mFullProgBarPaint: Paint? = null

    private var mThumbPaint: Paint? = null
    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mLabelText: String? = ""
    private val mTextBounds = Rect()
    private val mLabelIconPadding = Utils.dpToPx(4f, context)
    private var mLableIconDrawRect: Rect? = null
    private var mUnderlinePaint: Paint? = null
    private var mLableMarginRight: Int = 0
    private var mUnderlineMargin: Int = 0
    private var mTextTouchPadding: Int = 0

    private var mIsLabelClickable: Boolean = false

    internal var mSeekBarPercentListener: ((Float) -> Unit)? = null
    internal var mSeekbarValueListener: ((Int) -> Unit)? = null
    internal var mSeekBarLabelListener: (() -> Unit)? = null
    private var mLastEmittedValue: Int = 0
    private var mLabelIconBitmap: Bitmap? = null


    constructor(context: Context) : super(context) {
        init()

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        readStaticAttributes(attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        readStaticAttributes(attrs)
        init()

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        readStaticAttributes(attrs)
        init()

    }

    private fun readStaticAttributes(attrs: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar, 0, 0)

        try {
            setMinValue(typedArray.getInt(R.styleable.VerticalSeekBar_minValue, 0))
            setMaxValue(typedArray.getInt(R.styleable.VerticalSeekBar_maxValue, 100))
            setUpdateStep(typedArray.getInt(R.styleable.VerticalSeekBar_step, 1))

            setProgEmptyColor(typedArray.getColor(R.styleable.VerticalSeekBar_emptyProgressBarColor, -0x43303d))
            setProgFullColor(typedArray.getColor(R.styleable.VerticalSeekBar_fullProgressBarColor, -0xfc7335))
            setLableUnderlineColor(typedArray.getColor(R.styleable.VerticalSeekBar_underlineColor, -0x43403d))
            setLabelTextColor(typedArray.getColor(R.styleable.VerticalSeekBar_labelColor, -0xde8e51))

            setThumbDrawable(typedArray.getResourceId(R.styleable.VerticalSeekBar_thumbDrawable, R.drawable.ic_thumb))
            setLabelIconDrawable(typedArray.getResourceId(R.styleable.VerticalSeekBar_iconDrawable, R.drawable.ic_create_black_24dp))
            setProgressBarWidth(typedArray.getDimensionPixelSize(R.styleable.VerticalSeekBar_progressbarWidth, Utils.dpToPx(8f, context)))

            setTextSize(typedArray.getDimensionPixelSize(R.styleable.VerticalSeekBar_labelSize, Utils.spToPx(22f, context)))
            setLabelFont(ResourcesCompat.getFont(context, typedArray.getResourceId(R.styleable.VerticalSeekBar_labelTypeface, R.font.almoni_dl_aaa_regular)))
            setLabelText(typedArray.getString(R.styleable.VerticalSeekBar_labelText))
            setLabelClickable(typedArray.getBoolean(R.styleable.VerticalSeekBar_isClickable, false))

            setPercentProgress(typedArray.getFraction(R.styleable.VerticalSeekBar_percentFull, 1, 1, 0.5f))


        } finally {
            typedArray.recycle()
        }
    }


    fun setOnSeekPercentLisener(listener: (Float) -> Unit) {
        mSeekBarPercentListener = listener
    }

    fun setOnSeekValueListener(listener: (Int) -> Unit) {
        mSeekbarValueListener = listener
    }



    fun setOnLableClickListener(lableClickListener: () -> Unit) {
        mSeekBarLabelListener = lableClickListener
    }

    fun setPercentProgress(percent: Float) {
        updatePercent(percent)
    }

    fun setMaxValue(maxValue: Int) {
        mMaxValue = maxValue
    }

    fun setMinValue(minValue: Int) {
        mMinValue = minValue
    }

    fun setUpdateStep(updateStep: Int) {
        mUpdateStep = updateStep
    }

    fun setLableUnderlineColor(lableUnderlineColor: Int) {
        mLableUnderlineColor = lableUnderlineColor
    }

    fun setLabelTextColor(labelTextColor: Int) {
        mLabelTextColor = labelTextColor
    }

    fun setProgFullColor(progFullColor: Int) {
        mProgFullColor = progFullColor
    }

    fun setProgEmptyColor(progEmptyColor: Int) {
        mProgEmptyColor = progEmptyColor
    }

    fun setLabelClickable(labelClickable: Boolean) {
        mIsLabelClickable = labelClickable
    }

    fun setLabelIconDrawable(labelIconDrawable: Drawable) {
        mLabelIconDrawable = labelIconDrawable
    }

    fun setThumbDrawable(thumbDrawable: Drawable) {
        mThumbDrawable = thumbDrawable
    }

    fun setLabelIconDrawable(@DrawableRes labelIconRes: Int) {
        setLabelIconDrawable(ContextCompat.getDrawable(context, labelIconRes))
    }

    fun setThumbDrawable(@DrawableRes thumbRes: Int) {
        setThumbDrawable(ContextCompat.getDrawable(context, thumbRes))
    }

    fun setLabelText(text: String?) {
        if (!TextUtils.isEmpty(text)) {
            mLabelText = text
            mTextPaint.getTextBounds(text, 0, text!!.length, mTextBounds)
            invalidate()
        }
    }

    fun setLabelFont(labelFont: Typeface?) {
        mLabelFont = labelFont
    }

    fun setTextSize(textSizePx: Int) {
        mTextSize = textSizePx
    }

    fun setProgressBarWidth(progressBarWidth: Int) {
        this.progressBarRadius = progressBarWidth / 2
    }

    private fun init() {
        isSaveEnabled = true

        thumb = Thumb(this, mThumbDrawable, Utils.dpToPx(6f, context), Utils.dpToPx(4f,context))


        mLabelIconBitmap = Bitmap.createBitmap(mLabelIconDrawable!!.intrinsicWidth, mLabelIconDrawable!!.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val labelIcon = Canvas(mLabelIconBitmap!!)
        mLabelIconDrawable!!.setBounds(0, 0, labelIcon.width, labelIcon.height)
        mLabelIconDrawable!!.draw(labelIcon)
        mLableIconDrawRect = mLabelIconDrawable!!.bounds

        mThumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mThumbPaint!!.setShadowLayer(3f, -3f, 3f, -0x70000000)
        setLayerType(View.LAYER_TYPE_SOFTWARE, mThumbPaint)

        mEmptyProgBarPaint = Paint()
        mEmptyProgBarPaint!!.color = mProgEmptyColor
        mEmptyProgBarPaint!!.style = Paint.Style.FILL

        mFullProgBarPaint = Paint(mEmptyProgBarPaint)
        mFullProgBarPaint!!.color = mProgFullColor

        mTextPaint.textSize = mTextSize.toFloat()
        mTextPaint.color = mLabelTextColor
        mTextPaint.typeface = mLabelFont


        mUnderlinePaint = Paint()
        mUnderlinePaint!!.strokeWidth = Utils.dpToPx(1.5f, context).toFloat()
        mUnderlinePaint!!.style = Paint.Style.FILL_AND_STROKE
        mUnderlinePaint!!.color = mLableUnderlineColor

        mLableMarginRight = Utils.dpToPx(32f, context)
        mUnderlineMargin = Utils.dpToPx(10f, context)
        mTextTouchPadding = Utils.dpToPx(4f, context)


        invalidate()

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        centerHorizontal = measuredWidth / 2

        mScrollBarHeight = measuredHeight - paddingBottom - paddingTop - 2 * thumb.mThumbHalfHeight
        val inverseFillPercent = 1.0f - mPercentFull

        initialVertical = paddingTop + mScrollBarHeight * inverseFillPercent
        thumb.updateCenterPoint(centerHorizontal, initialVertical.toInt())
        mProgBar.set((centerHorizontal - progressBarRadius).toFloat(), (paddingTop + thumb.mThumbHalfHeight).toFloat(), (centerHorizontal + progressBarRadius).toFloat(), (measuredHeight - paddingBottom - thumb.mThumbHalfHeight).toFloat())
        mFullProgBarRect.set((centerHorizontal - progressBarRadius).toFloat(), thumb.getCenterPoint().y.toFloat(), (centerHorizontal + progressBarRadius).toFloat(), (measuredHeight - paddingBottom - thumb.mThumbHalfWidth).toFloat())
        mTextPaint.getTextBounds(mLabelText, 0, mLabelText!!.length, mTextBounds)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //draw background progressbar
        canvas.drawRoundRect(mProgBar, progressBarRadius.toFloat(), progressBarRadius.toFloat(), mEmptyProgBarPaint!!)

        //draw full-section progressbar
        mFullProgBarRect.top = thumb.getCenterPoint().y.toFloat()
        canvas.drawRoundRect(mFullProgBarRect, progressBarRadius.toFloat(), progressBarRadius.toFloat(), mFullProgBarPaint!!)

        //draw thumb location

        thumb.drawThumb(canvas)

        drawLabel(canvas)

    }

    private fun drawLabel(canvas: Canvas) {
        if (mLabelText != null) {

            canvas.drawText(mLabelText!!,
                    (thumb.getCenterPoint().x + mLableMarginRight).toFloat(),
                        (thumb.getCenterPoint().y - mTextBounds.centerY()).toFloat(),
                    mTextPaint)

            var underlineEnd = thumb.getCenterPoint().x + mLableMarginRight + mTextBounds.right

            if (mIsLabelClickable) {
                val rightOfTextWithPadding = thumb.getCenterPoint().x + mLableMarginRight + mTextBounds.right + mLabelIconPadding
                mLableIconDrawRect!!.offsetTo(rightOfTextWithPadding, thumb.getCenterPoint().y - mLableIconDrawRect!!.height() / 2)
                canvas.drawBitmap(mLabelIconBitmap!!, null, mLableIconDrawRect!!, null)
                //extend line under icon too
                underlineEnd += mLableIconDrawRect!!.width()
            }

            val underlieHeight = thumb.getCenterPoint().y - mTextBounds.centerY() + mUnderlineMargin
            canvas.drawLine((thumb.getCenterPoint().x + mLableMarginRight).toFloat(),
                    underlieHeight.toFloat(),
                    underlineEnd.toFloat(),
                    underlieHeight.toFloat(),
                    mUnderlinePaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            return determineDownTouchHandler(event)
        } else if (event.action == MotionEvent.ACTION_MOVE && isDragging) {
            moveThumb(event)
            return true
        } else if (event.action == MotionEvent.ACTION_UP) {
            isDragging = false
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun determineDownTouchHandler(event: MotionEvent): Boolean {
        return when {
            thumb.isWithinThumbBounds(event) -> {
                isDragging = true
                true
            }
            isWithinTextBounds(event) -> {
                handleLabelClick()
                true
            }
            else -> false
        }
    }

    private fun handleLabelClick() {

       if (mIsLabelClickable) {
           mSeekBarLabelListener?.invoke()
       }
    }


    private fun moveThumb(event: MotionEvent) {
        if (exceededMinBounds(event)) {
            notifyListeners(0.0f)
            return
        }
        if (exceededMaxBounds(event)) {
            notifyListeners(1.0f)
            return
        }

        thumb.updateCenterPoint(y = event.y.toInt())
        setPercentageWhileDrag(event)
        invalidate()

    }


    private fun isWithinTextBounds(event: MotionEvent): Boolean {
        val halfTextHeight = Math.abs(mTextBounds.top - mTextBounds.bottom) / 2
        return event.x >= thumb.getCenterPoint().x + mLableMarginRight - mTextTouchPadding &&
                event.x <= thumb.getCenterPoint().x + mLableMarginRight + mTextBounds.right + mTextTouchPadding &&
                event.y >= thumb.getCenterPoint().y - halfTextHeight - mTextTouchPadding &&
                event.y <= thumb.getCenterPoint().y + halfTextHeight + mTextTouchPadding
    }

    private fun setPercentageWhileDrag(event: MotionEvent) {
        val percentInPx = measuredHeight.toFloat() - event.y - paddingBottom.toFloat() - thumb.mThumbHalfHeight.toFloat()
        val percent = percentInPx / mScrollBarHeight
        notifyListeners(percent)

    }

    private fun notifyListeners(percent: Float) {
        updatePercent(percent)
        updateStepValue(percent)
    }

    private fun updatePercent(percent: Float) {
        if (percent != mPercentFull) {
            mPercentFull = percent
            mSeekBarPercentListener?.invoke(percent)?: return
        }
    }

    private fun updateStepValue(percent: Float) {
        val absoluteRange = mMaxValue - mMinValue

        mSeekbarValueListener?.let {
            val calculatedValue = ((absoluteRange * percent).toInt() + mMinValue).toFloat()
            val nearestStep = (calculatedValue / mUpdateStep).toInt() * mUpdateStep
            if (nearestStep != mLastEmittedValue) {
                mLastEmittedValue = nearestStep
                mSeekbarValueListener!!.invoke(nearestStep)
            }
        }

    }

    private fun exceededMaxBounds(event: MotionEvent): Boolean {
        return event.y - thumb.mThumbHalfHeight <= paddingTop
    }

    private fun exceededMinBounds(event: MotionEvent): Boolean {
        return event.y + thumb.mThumbHalfHeight > measuredHeight - paddingBottom
    }


    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ourState = SavedState(superState!!)
        ourState.savedPercentage = mPercentFull
        ourState.savedLabel = mLabelText
        return ourState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ourState = state as SavedState
        super.onRestoreInstanceState(ourState.superState)
        mPercentFull = ourState.savedPercentage
        mLabelText = ourState.savedLabel
        invalidate()
    }



}
