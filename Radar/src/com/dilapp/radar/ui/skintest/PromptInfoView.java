package com.dilapp.radar.ui.skintest;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.dilapp.radar.R;

/**
 * Created by husj1 on 2015/6/19.
 */
public class PromptInfoView {
    public final static int ID_INFOS = R.id.vg_infos;
    private Animation mShow = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, -1f, TranslateAnimation.RELATIVE_TO_SELF, 0f);
    private Animation mHide = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, -1f);
    private Context mContext;
    private View mContent;

    public PromptInfoView(Context context, View view) {
        this.mContext = context;
        this.mContent = view.findViewById(ID_INFOS);
        this.mContent.bringToFront();
        mShow.setDuration(200);
        mHide.setDuration(200);
    }

    public void setInfoText(int res, View.OnClickListener l) {
        setInfoText(res == 0 ? null : mContext.getString(res), l);
    }

    public void setInfoText(String text, View.OnClickListener l) {
        if (text == null || "".equals(text.trim())) {
            mContent.setOnClickListener(null);
            if (mContent.getVisibility() != View.GONE) {
                mContent.startAnimation(mHide);
                mContent.setVisibility(View.GONE);
            }
            return;
        }

        TextView tv_info_text = (TextView) mContent.findViewById(R.id.tv_info_text);
        tv_info_text.setText(text);
        mContent.setOnClickListener(l);
        if (mContent.getVisibility() != View.VISIBLE) {
            mContent.startAnimation(mShow);
            mContent.setVisibility(View.VISIBLE);
        }
    }

    public void setInfoIcon(int res) {
        ImageView iv = (ImageView) mContent.findViewById(R.id.iv_info_icon);
        if(res == 0) {
            if(iv.getVisibility() != View.GONE) {
                iv.setVisibility(View.GONE);
            }
        } else {
            if(iv.getVisibility() != View.VISIBLE) {
                iv.setVisibility(View.VISIBLE);
            }
            iv.setImageResource(res);
        }

    }
}
