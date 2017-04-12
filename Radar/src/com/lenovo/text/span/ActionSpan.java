package com.lenovo.text.span;

import java.util.Map;

import android.app.Activity;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.dilapp.radar.textbuilder.utils.L;

public class ActionSpan extends BaseClickSpan {

	private final static String TAG = L.makeTag(ActionSpan.class);
	private final static boolean LOG = false;

	private ClickableSpan dispatchSpan;
	private Map<String, String> params;

	public ActionSpan(Map<String, String> params) {
		this.params = params;
		init();
	}

	public void init() {
		if (params == null || !params.containsKey("type")) {
			return;
		}
		if (LOG)
			L.i(TAG, "type: " + params.get("type"));
		int type = Integer.parseInt(params.get("type"));
		switch (type) {
		case 1:
			dispatchSpan = new LabelSpan(params);
			break;
		case 2:
			dispatchSpan = new URLSpan(params);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View widget) {
		if (dispatchSpan != null) {
			dispatchSpan.onClick(widget);
		}
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		if (dispatchSpan != null) {
			try {
				dispatchSpan.updateDrawState(ds);
			} catch (Exception e) {
				L.w(TAG, "", e);
			}
		}
	}

	@Override
	public void setActivity(Activity activity) {
		if (dispatchSpan != null && dispatchSpan instanceof BaseClickSpan) {
			BaseClickSpan span = ((BaseClickSpan) dispatchSpan);
			span.setActivity(activity);
		}
	}
}
