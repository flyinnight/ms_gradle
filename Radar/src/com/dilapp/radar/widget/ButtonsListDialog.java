package com.dilapp.radar.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.util.DensityUtils;

import java.util.List;

/**
 * Created by husj1 on 2015/8/13.
 */
public class ButtonsListDialog extends BaseDialog {

    public final static float MIN_WIDTH = 260;// dip

    private List<ButtonsListItem> mButtonItems;

    public ButtonsListDialog(Activity acitvity, List<ButtonsListItem> buttonItems) {
        this(acitvity, R.style.FadeDialog, buttonItems);
    }

    public ButtonsListDialog(Activity acitvity, int theme, List<ButtonsListItem> buttonItems) {
        super(acitvity, theme);
        this.mButtonItems = buttonItems;
        setContentView(buildButtonsList());
        Window window = getWindow();
        window.getAttributes().width = DensityUtils.dip2px(mContext, MIN_WIDTH);
        // WindowManager wm = mActivity.getWindowManager();
    }

    private View buildButtonsList() {
        if (mButtonItems == null || this.mButtonItems.size() == 0) return null;

        // 设置好容器
        LinearLayout container = new LinearLayout(mContext);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setDividerDrawable(mContext.getResources().getDrawable(R.drawable.divider_default));
        container.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        // container.setMinimumWidth(DensityUtils.dip2px(mContext, MIN_WIDTH));
        final int size = mButtonItems.size();
        for (int i = 0; i < size; i++) {
            ButtonsListItem item = mButtonItems.get(i);
            if (item == null) continue;
            container.addView(buildButtonItem(mButtonItems.get(i)));
        }
        return container;
    }

    private View buildButtonItem(ButtonsListItem item) {
        if (item == null) return null;
        View itemView = mActivity.getLayoutInflater().inflate(R.layout.item_dialog_buttons_list, null);
        TextView btn = (TextView) itemView.findViewById(android.R.id.button1);
        btn.setId(item.getId());
        btn.setText(item.getText());
        btn.setOnClickListener(item.getOnClickListener());
        btn.setOnLongClickListener(item.getOnLongClickListener());
        return itemView;
    }

    public static class ButtonsListItem {
        private int id;
        private String text;
        private View.OnClickListener l;
        private View.OnLongClickListener ll;

        public ButtonsListItem() {
        }

        public ButtonsListItem(int id, String text, View.OnClickListener l) {
            this();
            this.id = id;
            this.text = text;
            this.l = l;
        }

        public ButtonsListItem(int id, String text, View.OnClickListener l, View.OnLongClickListener ll) {
            this(id, text, l);
            this.ll = ll;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public View.OnClickListener getOnClickListener() {
            return l;
        }

        public void setOnClickListener(View.OnClickListener l) {
            this.l = l;
        }

        public View.OnLongClickListener getOnLongClickListener() {
            return ll;
        }

        public void setOnLongClickListener(View.OnLongClickListener ll) {
            this.ll = ll;
        }
    }
}
