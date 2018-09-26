package com.onoapps.hanan.verticalseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author hanandann
 */

public class VerticalSeekBar extends View {

    private String TAG = this.getClass().getSimpleName();

    private int mLableUnderlineColor;
    private int mLabelTextColor;
    private int mProgFullColor;
    private int mProgEmptyColor;
    private float mPercentFull;
    int mMaxValue;
    int mMinValue;
    int mUpdateStep;
    private Typeface mLabelFont;
    private Drawable mLabelIconDrawable;
    private Drawable mThumbDrawable;
    private int mTextSize;

    private Bitmap mImgThumb;
    int centerHorizontal;
    Point thumbCenter;
    float initialVertical;

    boolean isDragging = false;

    int mThumbPaddingVertical;
    int mThumbPaddingHorizontal;


    private int mScrollBarHeight;

    int progressBarRadius = dpToPx(4, getContext());

    private RectF mProgBar;
    private Paint mEmptyProgBarPaint;
    private RectF mFullProgBarRect;
    private Paint mFullProgBarPaint;

    private Paint mThumbPaint;
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String mLabelText = "";
    private final Rect mTextBounds = new Rect();
    private int mLabelIconPadding = dpToPx(4, getContext());
    private Rect mLableIconDrawRect;
    private Paint mUnderlinePaint;
    private int mLableMarginRight;
    private int mUnderlineMargin;
    private int mTextTouchPadding;

    private boolean mIsLabelClickable;

    VerticalSeekBarPercentListener mSeekBarPercentListener;
    VerticalSeekbarValueListener mSeekbarValueListener;
    VerticalSeekBarLabelListener mSeekBarLabelListener;
    private int mLastEmittedValue;
    private Bitmap mLabelIconBitmap;


    public VerticalSeekBar(Context context) {
        super(context);
        init();

    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        readStaticAttributes(attrs);
        init();
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readStaticAttributes(attrs);
        init();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readStaticAttributes(attrs);
        init();

    }

    private void readStaticAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar, 0, 0);

        try {
            setMinValue(typedArray.getInt(R.styleable.VerticalSeekBar_minValue, 0));
            setMaxValue(typedArray.getInt(R.styleable.VerticalSeekBar_maxValue, 100));
            setUpdateStep(typedArray.getInt(R.styleable.VerticalSeekBar_step, 1));

            setProgEmptyColor(typedArray.getColor(R.styleable.VerticalSeekBar_emptyProgressBarColor, 0xFFBCCFC3));
            setProgFullColor(typedArray.getColor(R.styleable.VerticalSeekBar_fullProgressBarColor,  0xFF038CCB));
            setLableUnderlineColor(typedArray.getColor(R.styleable.VerticalSeekBar_underlineColor, 0xFFBCBFC3));
            setLabelTextColor(typedArray.getColor(R.styleable.VerticalSeekBar_labelColor, 0xFF2171AF));

            setThumbDrawable(typedArray.getResourceId(R.styleable.VerticalSeekBar_thumbDrawable, R.drawable.thumb));
            setLabelIconDrawable(typedArray.getResourceId(R.styleable.VerticalSeekBar_iconDrawable, R.drawable.ic_create_black_24dp));
            setProgressBarWidth(typedArray.getDimensionPixelSize(R.styleable.VerticalSeekBar_progressbarWidth, dpToPx(8, getContext())));

            setTextSize(typedArray.getDimensionPixelSize(R.styleable.VerticalSeekBar_labelSize, spToPx(22, getContext())));
            setLabelFont(ResourcesCompat.getFont(getContext(), typedArray.getResourceId(R.styleable.VerticalSeekBar_labelTypeface, R.font.almoni_dl_aaa_regular)));
            setLabelText(typedArray.getString(R.styleable.VerticalSeekBar_labelText));
            setLabelClickable(typedArray.getBoolean(R.styleable.VerticalSeekBar_isClickable, false));

            setPercentProgress(typedArray.getFraction(R.styleable.VerticalSeekBar_percentFull, 1,1 , 0.5f));






        } finally {
            typedArray.recycle();
        }
    }


    public void setOnSeekPercentLisener(VerticalSeekBarPercentListener listener) {
        mSeekBarPercentListener = listener;
    }

    public void setOnSeekValueListener(VerticalSeekbarValueListener listener) {
        mSeekbarValueListener = listener;
    }

    public void setOnLableClickListener(VerticalSeekBarLabelListener lableClickListener) {
        mSeekBarLabelListener = lableClickListener;
    }

    public void setPercentProgress(float percent) {
        updatePercent(percent);
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
    }

    public void setMinValue(int minValue) {
        mMinValue = minValue;
    }

    public void setUpdateStep(int updateStep) {
        mUpdateStep = updateStep;
    }

    public void setLableUnderlineColor(int lableUnderlineColor) {
        mLableUnderlineColor = lableUnderlineColor;
    }

    public void setLabelTextColor(int labelTextColor) {
        mLabelTextColor = labelTextColor;
    }

    public void setProgFullColor(int progFullColor) {
        mProgFullColor = progFullColor;
    }

    public void setProgEmptyColor(int progEmptyColor) {
        mProgEmptyColor = progEmptyColor;
    }

    public void setLabelClickable(boolean labelClickable) {
        mIsLabelClickable = labelClickable;
    }

    public void setLabelIconDrawable(Drawable labelIconDrawable) {
        mLabelIconDrawable = labelIconDrawable;
    }

    public void setThumbDrawable(Drawable thumbDrawable) {
        mThumbDrawable = thumbDrawable;
    }

    public void setLabelIconDrawable(@DrawableRes int labelIconRes) {
        setLabelIconDrawable(ContextCompat.getDrawable(getContext(), labelIconRes));
    }

    public void setThumbDrawable(@DrawableRes int thumbRes) {
        setThumbDrawable(ContextCompat.getDrawable(getContext(), thumbRes));
    }

    public void setLabelText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mLabelText = text;
            mTextPaint.getTextBounds(text, 0, text.length(), mTextBounds);
            invalidate();
        }
    }

    public void setLabelFont(Typeface labelFont) {
        mLabelFont = labelFont;
    }

    public void setTextSize(int textSizePx) {
        mTextSize = textSizePx;
    }

    public void setProgressBarWidth(int progressBarWidth) {
        this.progressBarRadius = progressBarWidth / 2;
    }

    private void init() {
        setSaveEnabled(true);
        thumbCenter = new Point();

        mThumbPaddingHorizontal = mThumbDrawable.getIntrinsicWidth() / 2;
        mThumbPaddingVertical = mThumbDrawable.getIntrinsicHeight() / 2;
        mImgThumb = Bitmap.createBitmap(mThumbDrawable.getIntrinsicWidth(), mThumbDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas thumbCanvas = new Canvas(mImgThumb);
        mThumbDrawable.setBounds(0, 0, thumbCanvas.getWidth(), thumbCanvas.getHeight());
        mThumbDrawable.draw(thumbCanvas);

        mLabelIconBitmap = Bitmap.createBitmap(mLabelIconDrawable.getIntrinsicWidth(), mLabelIconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas labelIcon = new Canvas(mLabelIconBitmap);
        mLabelIconDrawable.setBounds(0, 0, labelIcon.getWidth(), labelIcon.getHeight());
        mLabelIconDrawable.draw(labelIcon);
        mLableIconDrawRect = mLabelIconDrawable.getBounds();

        mProgBar = new RectF();
        mFullProgBarRect = new RectF();

        mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbPaint.setShadowLayer(12, -4, 4, 0x80000000);
        setLayerType(LAYER_TYPE_SOFTWARE, mThumbPaint);

        mEmptyProgBarPaint = new Paint();
        mEmptyProgBarPaint.setColor(mProgEmptyColor);
        mEmptyProgBarPaint.setStyle(Paint.Style.FILL);

        mFullProgBarPaint = new Paint(mEmptyProgBarPaint);
        mFullProgBarPaint.setColor(mProgFullColor);

        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mLabelTextColor);
        mTextPaint.setTypeface(mLabelFont);


        mUnderlinePaint = new Paint();
        mUnderlinePaint.setStrokeWidth(dpToPx(1.5f, getContext()));
        mUnderlinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mUnderlinePaint.setColor(mLableUnderlineColor);

        mLableMarginRight = dpToPx(32, getContext());
        mUnderlineMargin = dpToPx(10, getContext());
        mTextTouchPadding = dpToPx(4, getContext());


        invalidate();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerHorizontal = getMeasuredWidth() / 2;

        mScrollBarHeight = getMeasuredHeight() - getPaddingBottom() - getPaddingTop() - 2 * mThumbPaddingVertical;
        float inverseFillPercent = 1.0f - mPercentFull;

        initialVertical = getPaddingTop() + mScrollBarHeight * inverseFillPercent;
        thumbCenter.set(centerHorizontal, (int) initialVertical);
        mProgBar.set(centerHorizontal - progressBarRadius, getPaddingTop() + mThumbPaddingVertical, centerHorizontal + progressBarRadius, getMeasuredHeight() - getPaddingBottom() - mThumbPaddingVertical);
        mFullProgBarRect.set(centerHorizontal - progressBarRadius, thumbCenter.y, centerHorizontal + progressBarRadius, getMeasuredHeight() - getPaddingBottom() - mThumbPaddingVertical);
        mTextPaint.getTextBounds(mLabelText, 0, mLabelText.length(), mTextBounds);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw background progressbar
        canvas.drawRoundRect(mProgBar, progressBarRadius, progressBarRadius, mEmptyProgBarPaint);

        //draw full-section progressbar
        mFullProgBarRect.top = thumbCenter.y;
        canvas.drawRoundRect(mFullProgBarRect, progressBarRadius, progressBarRadius, mFullProgBarPaint);

        //draw thumb location
        if (mImgThumb != null) {
            canvas.drawBitmap(mImgThumb, thumbCenter.x - mThumbPaddingHorizontal, thumbCenter.y - mThumbPaddingVertical, mThumbPaint);
        }

        drawLabel(canvas);

    }

    private void drawLabel(Canvas canvas) {
        if (mLabelText != null) {

            canvas.drawText(mLabelText,
                    thumbCenter.x + mLableMarginRight,
                    thumbCenter.y - mTextBounds.centerY(),
                    mTextPaint);

            int underlineEnd = thumbCenter.x + mLableMarginRight + mTextBounds.right;

            if (mIsLabelClickable) {
                int rightOfTextWithPadding = thumbCenter.x + mLableMarginRight + mTextBounds.right + mLabelIconPadding;
                mLableIconDrawRect.offsetTo(rightOfTextWithPadding, thumbCenter.y - mLableIconDrawRect.height() / 2);
                canvas.drawBitmap(mLabelIconBitmap, null, mLableIconDrawRect, null);
                //extend line under icon too
                underlineEnd += mLableIconDrawRect.width();
            }

            int underlieHeight = thumbCenter.y - mTextBounds.centerY() + mUnderlineMargin;
            canvas.drawLine(thumbCenter.x + mLableMarginRight,
                    underlieHeight,
                    underlineEnd,
                    underlieHeight,
                    mUnderlinePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return determineDownTouchHandler(event);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && isDragging) {
            moveThumb(event);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isDragging = false;
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean determineDownTouchHandler(MotionEvent event) {
        if (isWithinThumbBounds(event)) {
            isDragging = true;
            return true;
        } else if (isWithinTextBounds(event)) {
            handleLabelClick();
            return true;
        } else return true;
    }

    private void handleLabelClick() {
        if (mSeekBarLabelListener != null && mIsLabelClickable) {
            mSeekBarLabelListener.onLabelClick();
        }
    }


    private void moveThumb(MotionEvent event) {
        if (exceededMinBounds(event)) {
            notifyListeners(0.0f);
            return;
        }
        if (exceededMaxBounds(event)) {
            notifyListeners(1.0f);
            return;
        }
        thumbCenter.y = (int) event.getY();
        setPercentageWhileDrag(event);
        invalidate();

    }

    private boolean isWithinThumbBounds(MotionEvent event) {
        return event.getY() >= thumbCenter.y - mThumbPaddingVertical &&
                event.getY() <= thumbCenter.y + mThumbPaddingVertical &&
                event.getX() >= thumbCenter.x - mThumbPaddingHorizontal &&
                event.getX() <= thumbCenter.x + mThumbPaddingHorizontal;
    }

    private boolean isWithinTextBounds(MotionEvent event) {
        int halfTextHeight = Math.abs(mTextBounds.top - mTextBounds.bottom) / 2;
        return event.getX() >= thumbCenter.x + mLableMarginRight - mTextTouchPadding &&
                event.getX() <= thumbCenter.x + mLableMarginRight + mTextBounds.right + mTextTouchPadding &&
                event.getY() >= thumbCenter.y - halfTextHeight - mTextTouchPadding &&
                event.getY() <= thumbCenter.y + halfTextHeight + mTextTouchPadding;
    }

    private void setPercentageWhileDrag(MotionEvent event) {
        float percentInPx = getMeasuredHeight() - event.getY() - getPaddingBottom() - mThumbPaddingVertical;
        float percent = percentInPx / mScrollBarHeight;
        notifyListeners(percent);

    }

    private void notifyListeners(float percent) {
        updatePercent(percent);
        updateStepValue(percent);
    }

    private void updatePercent(float percent) {
        if (percent != mPercentFull) {
            mPercentFull = percent;
            Log.d(TAG, "setPercentageWhileDrag: mPercentFull= " + mPercentFull);
            if (mSeekBarPercentListener != null) {
                mSeekBarPercentListener.percentSelected(percent);
            }
        }
    }

    private void updateStepValue(float percent) {
        int absoluteRange = mMaxValue - mMinValue;

        if (mSeekbarValueListener != null) {
            float calculatedValue = (int) (absoluteRange * percent) + mMinValue;
            int nearestStep = (int) (calculatedValue / mUpdateStep) * mUpdateStep;
            if (nearestStep != mLastEmittedValue) {
                mLastEmittedValue = nearestStep;
                mSeekbarValueListener.valueSelected(nearestStep);
            }
        }

    }

    private boolean exceededMaxBounds(MotionEvent event) {
        return event.getY() - mThumbPaddingVertical <= getPaddingTop();
    }

    private boolean exceededMinBounds(MotionEvent event) {
        return event.getY() + mThumbPaddingVertical > getMeasuredHeight() - getPaddingBottom();
    }


    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ourState = new SavedState(superState);
        ourState.savedPercentage = mPercentFull;
        ourState.savedLabel = mLabelText;
        return ourState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ourState = (SavedState) state;
        super.onRestoreInstanceState(ourState.getSuperState());
        mPercentFull = ourState.savedPercentage;
        mLabelText = ourState.savedLabel;
        invalidate();
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


    public interface VerticalSeekBarPercentListener {
        void percentSelected(float percent);
    }

    public interface VerticalSeekbarValueListener {
        void valueSelected(int value);
    }

    public interface VerticalSeekBarLabelListener {
        void onLabelClick();
    }


}
