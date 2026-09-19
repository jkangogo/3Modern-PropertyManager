package com.threemsystems.rentmanager;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Occupies the full window width but lays children out in a centered column
 * so forms and hubs stay readable on tablets, unfolded foldables, and split-screen.
 */
public class BoundedWidthLayout extends FrameLayout {
    private int maxWidthPx;
    private int hingeInsetPx;

    public BoundedWidthLayout(Context context) {
        super(context);
        init(context, null);
    }

    public BoundedWidthLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BoundedWidthLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        maxWidthPx = context.getResources().getDimensionPixelSize(R.dimen.form_max_width);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BoundedWidthLayout);
            maxWidthPx = array.getDimensionPixelSize(R.styleable.BoundedWidthLayout_boundedMaxWidth, maxWidthPx);
            array.recycle();
        }
    }

    public void setHingeInsetPx(int insetPx) {
        int next = Math.max(0, insetPx);
        if (hingeInsetPx == next) {
            return;
        }
        hingeInsetPx = next;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (parentWidth == 0 && MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            parentWidth = maxWidthPx;
        }
        int available = Math.max(0, parentWidth - hingeInsetPx * 2 - getPaddingLeft() - getPaddingRight());
        int childWidth = Math.max(0, Math.min(available, maxWidthPx));
        int childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int maxChildHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            measureChildWithMargins(child, childWidthSpec, 0, heightMeasureSpec, 0);
            maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
        }
        int height = maxChildHeight + getPaddingTop() + getPaddingBottom();
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(height, heightSize);
        }
        setMeasuredDimension(Math.max(parentWidth, childWidth), Math.max(height, getSuggestedMinimumHeight()));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        int available = Math.max(0, width - hingeInsetPx * 2 - getPaddingLeft() - getPaddingRight());
        int childWidth = Math.min(available, maxWidthPx);
        int childLeft = getPaddingLeft() + hingeInsetPx + Math.max(0, (available - childWidth) / 2);
        int childTop = getPaddingTop();
        int childBottom = height - getPaddingBottom();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            child.layout(childLeft, childTop, childLeft + childWidth, childBottom);
        }
    }
}
