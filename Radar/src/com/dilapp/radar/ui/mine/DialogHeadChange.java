package com.dilapp.radar.ui.mine;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.Button;

import com.dilapp.radar.R;
import com.dilapp.radar.widget.BaseDialog;

public class DialogHeadChange extends BaseDialog {
    private final static String TAG = DialogHeadChange.class.getSimpleName();
    private final static boolean LOG = true;
    public final static int ID_PHOTO = R.id.btn_photo;
    public final static int ID_TAKIN = R.id.btn_tak;
    public final static int ID_CANCEL = R.id.btn_cancel;

    private Button btn_photo;
    private Button btn_tak;
    private Button btn_cancel;

    public DialogHeadChange(Activity acitvity) {
        super(acitvity, R.style.BottomDialog);

        initView();
        initDialog();
    }

    private void initView() {
        setContentView(R.layout.dialog_head_change);

        btn_photo = findViewById_(R.id.btn_photo);
        btn_tak = findViewById_(R.id.btn_tak);
        btn_cancel = findViewById_(R.id.btn_cancel);
    }

    private void initDialog() {
        setWidthFullScreen();
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        setCanceledOnTouchOutside(true);
    }

    public void setButtonsOnClickListener(
            android.view.View.OnClickListener photo,
            android.view.View.OnClickListener takin,
            android.view.View.OnClickListener cancel) {
        btn_photo.setOnClickListener(photo);
        btn_tak.setOnClickListener(takin);
        btn_cancel.setOnClickListener(cancel);
    }

    private void l(String msg) {
        if (LOG) {
            Log.i(TAG, msg);
        }
    }
}
