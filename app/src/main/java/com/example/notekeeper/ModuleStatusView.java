package com.example.notekeeper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;

import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class ModuleStatusView extends View {
    public static final int EDIT_MODE_MODULE_COUNT = 7;
    public static final int INVALID_INDEX = -1;
    public static final int SHAPE_CIRCLE = 0;
    public static final float DEFAULT_OUTLINE_WIDTH_DP = 2f;
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;
    private float outlineWidth;
    private float shapeSize;
    private float spacing;
    private int outlineColor;
    private Paint paintOutline;
    private int fillColor;
    private Paint paintfill;
    private Rect[] moduleRectancle;
    private float radius;
    private int mMaxHorizontalModules;
    private int shape;
    private ModuleStatusAccessHelper accessHelper;


    public boolean[] getmModuleStatus() {
        return mModuleStatus;
    }

    public void setmModuleStatus(boolean[] mModuleStatus) {
        this.mModuleStatus = mModuleStatus;
    }

    private boolean[] mModuleStatus;
    public ModuleStatusView(Context context) {
        super(context);
        init(null, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        if(isInEditMode())
            setUpEditModeValues();

        setFocusable(true);
        accessHelper = new ModuleStatusAccessHelper(this);
        ViewCompat.setAccessibilityDelegate(this,accessHelper);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float displayDensity = dm.density;
        float defaultOutlineWidthPixels = displayDensity * DEFAULT_OUTLINE_WIDTH_DP;


        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ModuleStatusView, defStyle, 0);

        outlineColor = a.getColor(R.styleable.ModuleStatusView_outlineColor,Color.BLACK);
        shape = a.getInt(R.styleable.ModuleStatusView_shape, SHAPE_CIRCLE);
        outlineWidth = a.getDimension(R.styleable.ModuleStatusView_outlineWidth,defaultOutlineWidthPixels);

        a.recycle();


        shapeSize = 144f;
        spacing = 30f;
        radius = (shapeSize-outlineWidth)/2;



        paintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOutline.setStyle(Paint.Style.STROKE);
        paintOutline.setStrokeWidth(outlineWidth);
        paintOutline.setColor(outlineColor);

        fillColor = getContext().getResources().getColor(R.color.pluralsight_color);
        paintfill = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintfill.setStyle(Paint.Style.FILL);
        paintfill.setColor(fillColor);

        // Update TextPaint and text measurements from attributes
       // invalidateTextPaintAndMeasurements();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        accessHelper.onFocusChanged(gainFocus,direction,previouslyFocusedRect);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return accessHelper.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        return accessHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event);
    }

    private void setUpEditModeValues() {
        boolean[] exampleModuleValues = new boolean[EDIT_MODE_MODULE_COUNT];
        int middle = EDIT_MODE_MODULE_COUNT/2;
        for(int i = 0; i < middle;i++)
            exampleModuleValues[i] = true;

        setmModuleStatus(exampleModuleValues);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desireWidth = 0;
        int desireHeight = 0;

        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = specWidth - getPaddingLeft() - getPaddingRight();
        int horizontalModulesCanFit = (int)(availableWidth/(shapeSize + spacing));
        mMaxHorizontalModules = Math.min(horizontalModulesCanFit,mModuleStatus.length);


        desireWidth = (int) (mMaxHorizontalModules * (shapeSize + spacing) - spacing);
        desireWidth += getPaddingLeft() + getPaddingRight();

        int rows = ((mModuleStatus.length - 1) / mMaxHorizontalModules) +1;

        desireHeight = (int)((rows*(shapeSize + spacing)) - spacing);
        desireHeight += getPaddingTop() + getPaddingBottom();

        int width = resolveSizeAndState(desireWidth,widthMeasureSpec,0);
        int height = resolveSizeAndState(desireHeight,heightMeasureSpec,0);

        setMeasuredDimension(width,height);
    }

    private void setupModuleRectangles(int width) {
        int availableWidth = width - getPaddingLeft()-getPaddingRight();
        int horizontalModulesCanFit = (int)(availableWidth/(shapeSize+spacing));
        int maxHorizontalModules = Math.min(horizontalModulesCanFit,mModuleStatus.length);

        moduleRectancle = new Rect[mModuleStatus.length];
        for(int moduleIndex = 0; moduleIndex< moduleRectancle.length; moduleIndex++){
            int column = moduleIndex % maxHorizontalModules;
            int row = moduleIndex/maxHorizontalModules;
            int x = getPaddingLeft() + (int)(column * (shapeSize + spacing));
            int y = getPaddingTop() + (int)(row * (shapeSize + spacing));
            moduleRectancle[moduleIndex] = new Rect(x,y,x + (int)shapeSize,y + (int)shapeSize);



        }




    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setupModuleRectangles(w);
    }

    private void invalidateTextPaintAndMeasurements() {
       /* mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;

        */
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }

         */

        for (int moduleIndex = 0; moduleIndex < moduleRectancle.length; moduleIndex++) {
            if(shape == SHAPE_CIRCLE) {
                float x = moduleRectancle[moduleIndex].centerX();
                float y = moduleRectancle[moduleIndex].centerY();

                if (mModuleStatus[moduleIndex])
                    canvas.drawCircle(x, y, radius, paintfill);

                canvas.drawCircle(x, y, radius, paintOutline);
            }else{
                drawSquare(canvas,moduleIndex);
            }


        }

    }

    private void drawSquare(Canvas canvas , int moduleIndex){
        Rect moduleRectangle = moduleRectancle[moduleIndex];

        if(mModuleStatus[moduleIndex])
            canvas.drawRect(moduleRectangle,paintfill);

        canvas.drawRect(moduleRectangle.left + (outlineWidth/2),
                moduleRectangle.top + (outlineWidth/2),
                moduleRectangle.right + (outlineWidth/2),
                moduleRectangle.bottom +(outlineWidth/2),paintOutline);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                int moduleIndex = findItemAtPoint(event.getX(),event.getY());
                onModuleSelected(moduleIndex);
                return true;

        }


        return super.onTouchEvent(event);
    }

    private void onModuleSelected(int moduleIndex) {
        if(moduleIndex == INVALID_INDEX)
            return;

        mModuleStatus[moduleIndex] =! mModuleStatus[moduleIndex];
        invalidate();
        accessHelper.invalidateVirtualView(moduleIndex);
        accessHelper.sendEventForVirtualView(moduleIndex, AccessibilityEvent.TYPE_VIEW_CLICKED);

    }

    private int findItemAtPoint(float x, float y) {
        int moduleIndex = INVALID_INDEX;
        for(int i=0;i<moduleRectancle.length;i++){
            if(moduleRectancle[i].contains((int) x,(int)y)){
                moduleIndex = i;
                break;
            }
        }
        return moduleIndex;
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }


    private class ModuleStatusAccessHelper extends ExploreByTouchHelper{

        /**
         * Constructs a new helper that can expose a virtual view hierarchy for the
         * specified host view.
         *
         * @param host view whose virtual view hierarchy is exposed by this helper
         */
        public ModuleStatusAccessHelper(@NonNull View host) {
            super(host);
        }

        @Override
        protected int getVirtualViewAt(float x, float y) {

            int moduleIndex =  findItemAtPoint(x,y);
            return moduleIndex == INVALID_INDEX ? ExploreByTouchHelper.INVALID_ID : moduleIndex;
        }

        @Override
        protected void getVisibleVirtualViews(List<Integer> virtualViewIds) {
            if(moduleRectancle == null)
                return;

            for(int i = 0 ; i < moduleRectancle.length;i++){
                virtualViewIds.add(i);
            }
        }

        @Override
        protected void onPopulateNodeForVirtualView(int virtualViewId, @NonNull AccessibilityNodeInfoCompat node) {
            node.setFocusable(true);
            node.setBoundsInParent(moduleRectancle[virtualViewId]);
            node.setContentDescription("module " + virtualViewId);

            node.setCheckable(true);
            node.setChecked(mModuleStatus[virtualViewId]);

            node.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
        }

        @Override
        protected boolean onPerformActionForVirtualView(int virtualViewId, int action, @Nullable Bundle arguments) {
            switch (action){
                case AccessibilityNodeInfoCompat.ACTION_CLICK :
                    onModuleSelected(virtualViewId);
                    return true;

            }

            return false;
        }
    }
}
