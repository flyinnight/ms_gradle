package com.dilapp.radar.widget;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.dilapp.radar.R;

/**
 * Created by husj1 on 2015/11/5.
 */
public class PromptDialog extends BaseDialog {
    private TextView tv_title;
    private TextView tv_message;
    private Button btn_cancel;
    private Button btn_confirm;

    public PromptDialog(Activity acitvity) {
        super(acitvity, R.style.ShadowDialog);
        setContentView(R.layout.dialog_prompt);

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager wm = mActivity.getWindowManager();
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            lp.width = (int) (wm.getDefaultDisplay().getWidth() * .9f);
        } else {
            lp.width = (int) (wm.getDefaultDisplay().getHeight() * .9f);
        }
        window.setAttributes(lp);

        tv_title = findViewById_(com.dilapp.radar.R.id.tv_title);
        tv_message = findViewById_(com.dilapp.radar.R.id.tv_message);
        btn_cancel = findViewById_(com.dilapp.radar.R.id.btn_cancel);
        btn_confirm = findViewById_(com.dilapp.radar.R.id.btn_confirm);
    }

    public void setTitle(@StringRes int res) {
        tv_title.setText(res);
    }

    public void setTitle(CharSequence text) {
        tv_title.setText(text);
    }

    public CharSequence getTitle() {
        return tv_title.getText();
    }

    public void setMessage(@StringRes int res) {
        tv_message.setText(res);
    }

    public void setMessage(CharSequence text) {
        tv_message.setText(text);
    }

    public CharSequence getMessage() {
        return tv_message.getText();
    }

    public void setCancelOnClickListener(View.OnClickListener l) {
        btn_cancel.setOnClickListener(l);
    }

    public void setConfirmOnClickListener(View.OnClickListener l) {
        btn_confirm.setOnClickListener(l);
    }

    public void setCancelId(int id) {
        btn_cancel.setId(id);
    }

    public int getCancelId() {
        return btn_cancel.getId();
    }

    public void setConfirmId(int id) {
        btn_confirm.setId(id);
    }

    public int getConfirmId() {
        return btn_confirm.getId();
    }

    public void setTag(Object tag) {
        tv_title.setTag(tag);
    }

    public void setTag(int key, Object tag) {
        tv_title.setTag(key, tag);
    }

    public Object getTag() {
        return tv_title.getTag();
    }

    public Object getTag(int key) {
        return tv_title.getTag(key);
    }
}
