package com.dilapp.radar.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.dilapp.radar.R;

/**
 * This is a simple wrapper for {@link android.widget.LinearLayout} that implements the {@link android.widget.Checkable}
 * interface by keeping an internal 'checked' state flag.
 * <p/>
 * This can be used as the root view for a custom list item layout for
 * {@link android.widget.AbsListView} elements with a
 * {@link android.widget.AbsListView#setChoiceMode(int) choiceMode} set.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable, View.OnClickListener {
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private boolean mChecked;
    private boolean mItemChecked;

    private OnClickListener mClickListener;
    private OnCheckedChangeListener mCheckedChangeListener;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.CheckableLinearLayout,
                // com.android.internal.R.attr.radioButtonStyle
                R.attr.checked, 0);
        boolean checked = attributes.getBoolean(R.styleable.CheckableLinearLayout_checked, false);
        boolean itemChecked = attributes.getBoolean(R.styleable.CheckableLinearLayout_itemChecked, false);
        attributes.recycle();

        setChecked(checked);
        setItemChecked(itemChecked);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean b) {
        if (b != mChecked) {
            mChecked = b;
            refreshDrawableState();
            // Log.i(getClass().getName(), "checked is " + b);
            if (mCheckedChangeListener != null) {
                mCheckedChangeListener.onCheckedChanged(this, mChecked);
            }
        }
    }

    public boolean isItemChecked() {
        return mItemChecked;
    }

    public void setItemChecked(boolean itemChecked) {
        this.mItemChecked = itemChecked;
        if(this.mItemChecked) {
            super.setOnClickListener(null);
            super.setClickable(false);
        } else {
            // super.setClickable(true);
            super.setOnClickListener(this);
        }
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public void onClick(View v) {
        // if(!mChecked) {
            toggle();
        // }
        if (mClickListener != null) {
            mClickListener.onClick(v);
        }
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return mCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.mCheckedChangeListener = l;
    }

    public OnClickListener getOnClickListener() {
        return mClickListener;
    }

    public void setOnClickListener(OnClickListener l) {
        this.mClickListener = l;
    }

    public interface OnCheckedChangeListener {

        void onCheckedChanged(CheckableLinearLayout view, boolean isChecked);
    }
}