package com.onoapps.hanan.verticalseekbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
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
import android.view.MotionEvent
import android.view.View

/**
 * @author hanandann
 */

class VerticalSeekBar : View {

    private val TAG = this.javaClass.simpleName

    private var mProgFullColor: Int = 0
    private var mProgEmptyColor: Int = 0
    private var mPercentFull: Float = 0.toFloat()
    internal var mMaxValue: Int = 0
    internal var mMinValue: Int = 0
    internal var mUpdateStep: Int = 0

    internal var centerHorizontal: Int = 0

    internal var initialVertical: Float = 0.toFloat()

    internal var isDragging = false


    private lateinit var thumb: Thumb
    private lateinit var label: Label


    private var mScrollBarHeight: Int = 0

    internal var progressBarRadius = Utils.dpToPx(4f, context)

    private val mProgBar: RectF = RectF()
    private var mEmptyProgBarPaint: Paint? = null
    private val mFullProgBarRect: RectF = RectF()
    private var mFullProgBarPaint: Paint? = null

    private var mLabelText: String? = ""

    private var mTextTouchPadding: Int = 0


    internal var mSeekBarPercentListener: ((Float) -> Unit)? = null
    internal var mSeekbarValueListener: ((Int) -> Unit)? = null
    internal var mSeekBarLabelListener: (() -> Unit)? = null
    private var mLastEmittedValue: Int = 0


    constructor(context: Context) : super(context) {
        init()

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        loadExtraObjetcs()
        readStaticAttributes(attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        loadExtraObjetcs()
        readStaticAttributes(attrs)
        init()

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        loadExtraObjetcs()
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
        label.lableUnderlineColor = lableUnderlineColor
    }

    fun setLabelTextColor(labelTextColor: Int) {
        label.labelTextColor = labelTextColor
    }

    fun setProgFullColor(progFullColor: Int) {
        mProgFullColor = progFullColor
    }

    fun setProgEmptyColor(progEmptyColor: Int) {
        mProgEmptyColor = progEmptyColor
    }

    fun setLabelClickable(labelClickable: Boolean) {
        label.mIsLabelClickable = labelClickable
    }

    fun setLabelIconDrawable(labelIconDrawable: Drawable) {
//        mLabelIconDrawable = labelIconDrawable
        label.setIconDrawable(labelIconDrawable)
    }

    fun setThumbDrawable(thumbDrawable: Drawable) {
        thumb.mThumbDrawable = thumbDrawable
    }

    fun setLabelIconDrawable(@DrawableRes labelIconRes: Int) {
        setLabelIconDrawable(ContextCompat.getDrawable(context, labelIconRes))

    }

    fun setThumbDrawable(@DrawableRes thumbRes: Int) {
        setThumbDrawable(ContextCompat.getDrawable(context, thumbRes))
    }

    fun setLabelText(text: String?) {
        if (!TextUtils.isEmpty(text)) {
            label.mLabelText = text!!

//            mLabelText = text
//            mTextPaint.getTextBounds(text, 0, text!!.length, mTextBounds)
            invalidate()
        }
    }

    fun setLabelFont(labelFont: Typeface?) {
        label.labelFont = labelFont?:return
    }

    fun setTextSize(textSizePx: Int) {
        label.mTextSize = textSizePx
    }

    fun setProgressBarWidth(progressBarWidth: Int) {
        this.progressBarRadius = progressBarWidth / 2
    }

    private fun loadExtraObjetcs() {
        thumb = Thumb(this, Utils.dpToPx(6f, context), Utils.dpToPx(4f, context))
        label = Label(this, Label.Side.RIGHT)
    }
    private fun init() {
        isSaveEnabled = true

        thumb.init()
        label.init()

        mEmptyProgBarPaint = Paint()
        mEmptyProgBarPaint!!.color = mProgEmptyColor
        mEmptyProgBarPaint!!.style = Paint.Style.FILL

        mFullProgBarPaint = Paint(mEmptyProgBarPaint)
        mFullProgBarPaint!!.color = mProgFullColor

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
//        mTextPaint.getTextBounds(mLabelText, 0, mLabelText!!.length, mTextBounds)
        label.mLabelText = mLabelText?:""
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
        label.drawLabel(canvas, thumb.mContainingRect)



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
            label.isWithinTextBounds(event) -> {
                handleLabelClick()
                true
            }
            else -> false
        }
    }

    private fun handleLabelClick() {

        if (label.mIsLabelClickable) {
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
            mSeekBarPercentListener?.invoke(percent) ?: return
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
