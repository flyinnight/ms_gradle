package com.dilapp.radar.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.dilapp.radar.R;

/**
 * Created by husj1 on 2015/7/20.
 */
public class ButtonsDialog extends BaseDialog {

    private Activity acitvity;
    private List<ButtonGroup> groups;
    private LinearLayout rootView;

    public ButtonsDialog(Activity acitvity) {
        this(acitvity, R.style.BottomDialog);
    }

    public ButtonsDialog(Activity acitvity, int theme) {
        super(acitvity, theme);
        this.acitvity = acitvity;
        this.groups = new ArrayList<ButtonGroup>();

        setWidthFullScreen();
        getWindow().setGravity(Gravity.BOTTOM);

        Resources res = acitvity.getResources();
        rootView = new LinearLayout(acitvity);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setDividerDrawable(res.getDrawable(R.drawable.divider_transparent));
        rootView.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_END);

        setCanceledOnTouchOutside(true);

        setContentView(rootView);
    }

    public List<ButtonGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ButtonGroup> groups) {
        if(groups == null) {
            this.groups = null;
            rootView.removeAllViews();
            return;
        }
        if(this.groups != groups) {
            this.groups = groups;

            for (int i = 0; i < this.groups.size(); i++) {
                ButtonGroup group = this.groups.get(i);
                if(group == null) continue;
                rootView.addView(group.layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    public static class ButtonGroup {
        private LinearLayout layout;
        private List<ButtonItem> buttons;

        public ButtonGroup(Context context) {
            buttons = new LinkedList<ButtonItem>();
            layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setDividerDrawable(context.getResources().getDrawable(R.drawable.divider_default));
            layout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        }

        public void add(ButtonItem item) {
            if(item == null) return;
            buttons.add(item);
            layout.addView(item.button);
        }

        public void remove(int index) {
            buttons.remove(index);
            layout.removeViewAt(index);
        }

        public void remove(ButtonItem item) {
            buttons.remove(item);
            layout.removeView(item.button);
        }

        public void set(int index, ButtonItem item) {
            buttons.set(index, item);
            layout.addView(item.button, index);
        }
    }


    public static class ButtonItem {
        private Button button;

        public ButtonItem(Context context) {
            this(context, R.style.BottomDialog_Button);
        }

        public ButtonItem(Context context, int id, CharSequence text, int color, int sizePx, boolean enabled, View.OnClickListener l) {
            this(context);
            button.setId(id);
            button.setText(text);
            button.setTextColor(color);
            if(sizePx > 0) {
                button.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizePx);
            }
            button.setEnabled(enabled);
            button.setOnClickListener(l);
            // button.setBackgroundResource(R.drawable.btn_white_radius_none);
        }

        public ButtonItem(Context context, int style) {
            try {
                Class clazz = Class.forName("com.android.internal.R$attr");
                Field field = clazz.getField("buttonStyle");
                int defStyle = field.getInt(clazz);
                android.util.Log.i("III", "--defStyle " + defStyle);
                button = new Button(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setId(int id) {
            button.setId(id);
        }

        public void setText(CharSequence text) {
            button.setText(text);
        }

        public void setText(int res) {
            button.setText(res);
        }

        public void setTextSize(int px) {
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
        }

        public void setTextColor(int color) {
            button.setTextColor(color);
        }

        public void setOnClickListener(View.OnClickListener l) {
            button.setOnClickListener(l);
        }

        public void setClickable(boolean clickable) {
            button.setClickable(clickable);
        }

        public void setEnabled(boolean enabled) {
            button.setEnabled(enabled);
        }

        public Button getButton() {
            return button;
        }
    }
}
