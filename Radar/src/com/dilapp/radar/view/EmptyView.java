package com.dilapp.radar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dilapp.radar.R;

import java.lang.reflect.Field;

import static com.dilapp.radar.textbuilder.utils.L.*;

public class EmptyView extends FrameLayout {

    private View mContent;
    private TextView mEmptyText;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContent = LayoutInflater.from(context).inflate(R.layout.layout_empty_data, this, true);
        mEmptyText = (TextView) mContent.findViewById(android.R.id.text1);

        try {
            Class clazz = context.getClassLoader().loadClass("com.android.internal.R$styleable");
            Field field = clazz.getField("TextView");
            field.setAccessible(true);
            int[] styleable = (int[]) field.get(clazz);
            field = clazz.getField("TextView_text");
            int index = field.getInt(clazz);

            clazz = context.getClassLoader().loadClass("android.R$attr");
            field = clazz.getField("text");
            field.setAccessible(true);
            int attr = field.getInt(clazz);

            String log = "";
            for (int i = 0; i < styleable.length; i++) {
                log += styleable[i] + (i == styleable.length - 1 ? "" : ",");
            }
            // d("III_view", "styleable " + log + ", attr " + attr);
            TypedArray attributes = context.obtainStyledAttributes(attrs, styleable, attr, 0);
            CharSequence text = attributes.getText(index);
            attributes.recycle();

            mEmptyText.setText(text);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setText(int res) {
        mEmptyText.setText(res);
    }

    public void setText(CharSequence text) {
        mEmptyText.setText(text);
    }

    public CharSequence getText() {
        return mEmptyText.getText();
    }
}
