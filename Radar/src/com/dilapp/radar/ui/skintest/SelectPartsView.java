package com.dilapp.radar.ui.skintest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.RadioButton;
import com.dilapp.radar.R;
import com.dilapp.radar.view.RelativeRadioGroup;
import com.dilapp.radar.view.RelativeRadioGroup.OnCheckedChangeListener;

public class SelectPartsView {
    private final static String TAG = SelectPartsView.class.getSimpleName();
    private final static boolean LOG = false;

    // 日常测试Button的文本
    public final static int[] NORMAL_BUTTONS_TEXT = new int[]{
            R.string.normal_forehead, R.string.normal_cheek,
            R.string.normal_eye, R.string.normal_nose,
            R.string.normal_hand
    };
    // 日常测试Button的Flag
    public final static int[] NORMAL_BUTTONS_FLAG = new int[]{
            R.string.normal_forehead, R.string.normal_cheek,
            R.string.normal_eye, R.string.normal_nose,
            R.string.normal_hand
    };

    // 肤质测试Button的文本
    public final static int[] SKIN_BUTTONS_TEXT = new int[]{
            R.string.normal_nose, R.string.normal_forehead,
            R.string.normal_eye
    };
    // 肤质测试Button的Flag
    public final static int[] SKIN_BUTTONS_FLAG = new int[]{
            R.string.normal_nose, R.string.normal_forehead,
            R.string.normal_eye
    };
    // 护肤品测试Button的文本
    public final static int[] TASTE_BUTTONS_TEXT = new int[]{
            R.string.normal_forehead, R.string.normal_cheek, R.string.normal_nose
    };
    // 护肤品测试Button的Flag
    public final static int[] TASTE_BUTTONS_FLAG = new int[]{
            R.string.normal_forehead, R.string.normal_cheek, R.string.normal_nose
    };

    private Context mContext;
    private RelativeRadioGroup mContainer;

    private ViewGroup vg_circle_border;
    private RadioButton[] buttons = new RadioButton[5];
    private String[] buttonsText;
    private int[] buttonsId;
    private int[] buttonsFlag;
    //	private RadioButton rb_eye;
//	private RadioButton rb_cheek;
//	private RadioButton rb_forehead;
//	private RadioButton rb_nose;
//	private RadioButton rb_hand;

    public SelectPartsView(Context context, RelativeRadioGroup radioGroup, String[] btnsText, int[] btnsFlag) {
        this.mContext = context;
        this.mContainer = radioGroup;

        this.buttonsText = btnsText;
        this.buttonsFlag = btnsFlag;
        this.buttonsId = new int[buttonsText.length];

        init();
    }

    private void init() {
        findViews();

        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        final int screenWidth = wm.getDefaultDisplay().getWidth();
        final int distance = mContext.getResources().getDimensionPixelSize(
                R.dimen.test_normal_circle_border_distance);

        final int viewWidth = screenWidth - distance * 2;
        final int viewHeight = viewWidth;
        final int borderWidth = mContext.getResources().getDimensionPixelSize(
                R.dimen.test_normal_circle_border_width);
        final int rbRound = mContext.getResources().getDimensionPixelSize(
                R.dimen.test_normal_radio_round);
        final int circleMarginTop = rbRound / 2 + rbRound / 4 - borderWidth;
        final int twoMarginT = viewHeight / 4 - rbRound/*
                                                         * / 2 - ( rbHeight -
														 * circleMarginTop )
														 */;
        final int twoMarginLR = rbRound / 4;
        final int threeMarginT = (viewHeight / 4 * 3 - (int) (rbRound * 1.5f))
                - (twoMarginT + rbRound);
        final int threeMarginLR = twoMarginLR;

        ((MarginLayoutParams) buttons[1].getLayoutParams()).topMargin = twoMarginT;
        ((MarginLayoutParams) buttons[1].getLayoutParams()).leftMargin = twoMarginLR;
        ((MarginLayoutParams) buttons[2].getLayoutParams()).topMargin = twoMarginT;
        ((MarginLayoutParams) buttons[2].getLayoutParams()).rightMargin = twoMarginLR;
        ((MarginLayoutParams) buttons[3].getLayoutParams()).topMargin = threeMarginT;
        ((MarginLayoutParams) buttons[3].getLayoutParams()).leftMargin = threeMarginLR;
        ((MarginLayoutParams) buttons[4].getLayoutParams()).topMargin = threeMarginT;
        ((MarginLayoutParams) buttons[4].getLayoutParams()).rightMargin = threeMarginLR;
        ((MarginLayoutParams) vg_circle_border.getLayoutParams()).topMargin = circleMarginTop;
        l("top pixel is " + circleMarginTop + ", two top is " + twoMarginT + ", left is " + twoMarginLR + ", right is " + threeMarginLR);

        mContainer.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @SuppressLint("NewApi")
					@Override
                    public void onGlobalLayout() {
                        // l("radio button width is "
                        // + rb_forehead.getMeasuredWidth()
                        // + ", height is "
                        // + rb_forehead.getMeasuredHeight());
                        View child = vg_circle_border.getChildAt(0);
                        l("img w is " + child.getMeasuredWidth() + ", h is "
                                + child.getMeasuredHeight());
                        LayoutParams params = vg_circle_border.getChildAt(1)
                                .getLayoutParams();
                        params.width = child.getMeasuredWidth();
                        params.height = child.getMeasuredHeight();

                        if (Build.VERSION.SDK_INT < 16) {
                            mContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
        // ViewUtils.measureView(rb_forehead);
        l("circle width is " + viewWidth + ", height is " + viewHeight);
        vg_circle_border.getLayoutParams().width = viewWidth;
        vg_circle_border.getLayoutParams().height = viewHeight;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mContainer.setOnCheckedChangeListener(listener);
        listener.onCheckedChanged(mContainer, mContainer.getCheckedRadioButtonId());
    }

    public int getCheckedRadioButtonFlag() {
        return (Integer) mContainer.findViewById(mContainer.getCheckedRadioButtonId()).getTag();
    }

    public int getCheckedRadioButtonId() {
        return mContainer.getCheckedRadioButtonId();
    }

    public void setEnableRadioButton(int id, boolean enable) {
        mContainer.findViewById(id).setEnabled(enable);
    }

    public int[] getRadioButtonsId() {
        return buttonsId;
    }

    public void setChecked(int id, boolean isChecked) {
        if(id == mContainer.getCheckedRadioButtonId()) {
            return;
        }
        ((RadioButton)mContainer.findViewById(id)).setChecked(isChecked);
    }

    private void findViews() {
        int a = 0;
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            View child = mContainer.getChildAt(i);
            if (child instanceof ViewGroup) {
                vg_circle_border = (ViewGroup) child;
            } else if (child instanceof RadioButton) {
                buttons[a] = (RadioButton) child;
                if (a < buttonsText.length) {
                    buttons[a].setText(buttonsText[a]);
                } else {
                    child.setVisibility(View.GONE);
                }
                if (a < buttonsFlag.length) {
                    buttons[a].setTag(buttonsFlag[a]);
                    buttonsId[a] = buttons[a].getId();
                }
                a++;
            }
        }
    }

    private static void l(String msg) {
        if (LOG)
            Log.d(TAG, msg);
    }
}
