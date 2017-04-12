package com.lenovo.text.span;

import android.app.Activity;
import android.text.style.ClickableSpan;

public abstract class BaseClickSpan extends ClickableSpan {

	private Activity activity;

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Activity getActivity() {
		return activity;
	}
}
