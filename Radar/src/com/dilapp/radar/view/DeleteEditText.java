package com.dilapp.radar.view;

import com.dilapp.radar.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

/**
 * 
 * @author changtaoxie
 *
 */
public class DeleteEditText extends EditText implements OnFocusChangeListener,
		TextWatcher {

	private Drawable deleteBackground;
	private boolean hasFoucus;

	private boolean isClearAll = true;

	private boolean isShowClearBtn = false;

	private RightKeyOnClickListener rightKeyOnClick;

	private OnTextChangeListener onTextChangeListener;

	private View otherView;

	public DeleteEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(attrs);
	}

	public DeleteEditText(Context context, AttributeSet attrs) {

		this(context, attrs, android.R.attr.editTextStyle);
	}

	public DeleteEditText(Context context) {
		super(context, null);
	}

	private void init(AttributeSet attrs) {
		deleteBackground = getCompoundDrawables()[2];
		if (deleteBackground == null) {
			deleteBackground = getResources().getDrawable(
					R.drawable.delete_img);
		}
		deleteBackground.setBounds(0, 0, deleteBackground.getIntrinsicWidth(),
				deleteBackground.getIntrinsicHeight());

		setDeleteIconVisible(false);

		setOnFocusChangeListener(this);

		addTextChangedListener(this);

		// if (attrs != null) {
		// // 瀵瑰簲ATTR
		// TypedArray typeArray = getContext().obtainStyledAttributes(attrs,
		// R.styleable.input);
		// this.isClearAll = typeArray.getBoolean(R.styleable.input_isClearAll,
		// true);
		// this.isShowClearBtn =
		// typeArray.getBoolean(R.styleable.input_isShowClearBtn, true);
		// if (!isShowClearBtn) {
		// deleteBackground = null;
		// }
		// // 閲婃斁
		// typeArray.recycle();
		// }
	}

	public void setDeleteIconVisible(boolean visible) {
		if (otherView != null) {
			if (visible) {
				otherView.setVisibility(View.VISIBLE);
			} else {
				otherView.setVisibility(View.INVISIBLE);
			}
		}
		Drawable right = visible ? deleteBackground : null;
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	public void isShowPassword(boolean isShow) {
		if (isShow) {

			this.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
		} else {

			this.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {
				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight())));
				if (touchable) {

					if (rightKeyOnClick != null) {
						rightKeyOnClick.click(this);
					} else {
						if (isClearAll) {
							this.setText("");
						} else {
							String text = this.getText().toString();
							text = text.substring(0, text.length() - 1);
							this.setText(text);
						}
					}
				}
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
		if (hasFoucus) {

			setDeleteIconVisible(s.length() > 0);
		}
		if (onTextChangeListener != null) {
			onTextChangeListener.textChangeListener(this, s);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		this.hasFoucus = hasFocus;
		if (hasFocus) {
			setDeleteIconVisible(getText().length() > 0);
		} else {
			setDeleteIconVisible(false);
		}
	}

	public void setShakeAnimation() {
		this.setAnimation(shakeAnimation(5));
	}

	public void isClearAll(boolean isClearAll) {
		this.isClearAll = isClearAll;
	}

	public void isShowClearBtn(boolean isShowClearBtn) {
		this.isShowClearBtn = isShowClearBtn;
	}

	private Animation shakeAnimation(int counts) {
		Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
		translateAnimation.setInterpolator(new CycleInterpolator(counts));
		translateAnimation.setDuration(1000);
		return translateAnimation;
	}

	public void hasFocus(boolean hasFocus) {
		this.hasFoucus = hasFocus;
	}

	public void clearAll() {
		this.setText("");
	}

	public synchronized void setCheckOkBg(int resId) {
		if (resId == 0) {
			setCompoundDrawables(getCompoundDrawables()[0],
					getCompoundDrawables()[1], getCompoundDrawables()[2],
					getCompoundDrawables()[3]);
			return;
		}
		Drawable right = getResources().getDrawable(resId);
		if (right == null) {
			return;
		}
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	public void setRightKeyOnClick(RightKeyOnClickListener rightKeyOnClick) {
		this.rightKeyOnClick = rightKeyOnClick;
	}

	public void setOnTextChangeListener(
			OnTextChangeListener onTextChangeListener) {
		this.onTextChangeListener = onTextChangeListener;
	}

	public void setOtherView(View view) {
		this.otherView = view;
	}

	public void setEnabled(boolean enabled) {
		setDeleteIconVisible(false);
		super.setEnabled(enabled);
	}

	public interface RightKeyOnClickListener {
		public void click(View view);
	}

	public interface OnTextChangeListener {
		public void textChangeListener(View view, CharSequence text);
	}
}
