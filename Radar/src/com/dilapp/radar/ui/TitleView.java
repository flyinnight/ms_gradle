package com.dilapp.radar.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dilapp.radar.R;

/**
 * 全局的标题
 */
public class TitleView {

	public final static int ID_TITLE = R.id.vg_toolbar;

	public final static int ID_LEFT = R.id.vg_left;
	public final static int ID_RIGHT = R.id.vg_right;
	public final static int ID_CENTER = R.id.vg_center;
	private final static int ID_LEFT_TEXT = R.id.tv_left;
	private final static int ID_CENTER_TEXT = R.id.tv_center;
	private final static int ID_RIGHT_TEXT = R.id.tv_right;
	protected Context mContext;
	protected View mTitle;

	public TitleView(Context context, View view) {
		this.mContext = context;
		this.mTitle = view;
	}

	/**
	 * 将Title适配虚拟键盘
	 */
	public void adaptVirtualKeyboard() {
		// 如果没有虚拟键盘就把Title设置成没有虚拟键盘的高度
		if (!ViewConfiguration.get(mContext).hasPermanentMenuKey()) {
			int h = mContext.getResources().getDimensionPixelSize(
					R.dimen.title_height_virtual_keyboard);
			setHeight(h);
		}
	}

	public void setVisibility(int visibility) {
		mTitle.setVisibility(visibility);
	}

	public int getVisibility() {
		return mTitle.getVisibility();
	}

	public int getHeight() {
		return this.mTitle.getLayoutParams().height;
	}

	public void setHeight(int height) {
		this.mTitle.getLayoutParams().height = height;
	}

	public void setLeftText(@StringRes int res, OnClickListener l) {
		setLeftText(mContext.getResources().getText(res), l);
	}

	public void setLeftText(CharSequence text, OnClickListener l) {
		setText(ID_LEFT, ID_LEFT_TEXT, text, l);
	}

	public void setCenterText(@StringRes int res, OnClickListener l) {
		setCenterText(mContext.getResources().getText(res), l);
	}

	public void setCenterText(CharSequence text, OnClickListener l) {
		setText(R.id.vg_center, ID_CENTER_TEXT, text, l);
	}

	public void setRightText(@StringRes int res, OnClickListener l) {
		setRightText(mContext.getResources().getText(res), l);
	}

	public void setRightText(CharSequence text, OnClickListener l) {
		setText(ID_RIGHT, ID_RIGHT_TEXT, text, l);
	}

	public void setLeftIcon(@DrawableRes int res, OnClickListener l) {
		setLeftIcon(mContext.getResources().getDrawable(res), l);
	}

	public void setLeftIcon(Drawable drawable, OnClickListener l) {
		setIcon(ID_LEFT, R.id.iv_left, drawable, l);
	}

	public void setCenterIcon(@DrawableRes int res, OnClickListener l) {
		setCenterIcon(mContext.getResources().getDrawable(res), l);
	}

	public void setCenterIcon(Drawable drawable, OnClickListener l) {
		setIcon(R.id.vg_center, R.id.iv_center, drawable, l);
	}

	public void setRightIcon(@DrawableRes int res, OnClickListener l) {
		if (res <= 0) {
			setRightIcon(null, l);
		} else {
			setRightIcon(mContext.getResources().getDrawable(res), l);
		}
	}

	public void setRightIcon(Drawable drawable, OnClickListener l) {
		setIcon(ID_RIGHT, R.id.iv_right, drawable, l);
	}

	public void setBackgroundResource(int res) {
		// mTitle.findViewById(android.R.id.widget_frame).setBackgroundResource(res);
		mTitle.setBackgroundResource(res);
	}

	public void setBackground(Drawable bg) {
		// mTitle.findViewById(android.R.id.widget_frame).setBackground(bg);
		mTitle.setBackground(bg);
	}

	public void setBackgroundColor(int color) {
		// mTitle.findViewById(android.R.id.widget_frame).setBackgroundColor(color);
		mTitle.setBackgroundColor(color);
	}

	public void setTextColor(int color) {
		((TextView) mTitle.findViewById(ID_LEFT_TEXT)).setTextColor(color);
		((TextView) mTitle.findViewById(ID_CENTER_TEXT)).setTextColor(color);
		((TextView) mTitle.findViewById(ID_RIGHT_TEXT)).setTextColor(color);
	}

	private void setIcon(int vgId, int ivId, Drawable drawable,
			OnClickListener l) {

		View vg = mTitle.findViewById(vgId);
		ImageView iv = (ImageView) mTitle.findViewById(ivId);

		if (drawable != null) {
			iv.setImageDrawable(drawable);
			iv.setVisibility(View.VISIBLE);
		} else {
			iv.setVisibility(View.GONE);
		}

		vg.setOnClickListener(l);
		vg.setVisibility(View.VISIBLE);
	}

	private void setText(int vgId, int tvId, CharSequence text,
			OnClickListener l) {

		ViewGroup vg = (ViewGroup) mTitle.findViewById(vgId);
		TextView tv = (TextView) mTitle.findViewById(tvId);

		if (text != null) {
			tv.setText(text);
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setText("");
			tv.setVisibility(View.GONE);
		}

		vg.setOnClickListener(l);
		vg.setVisibility(View.VISIBLE);
	}
}
