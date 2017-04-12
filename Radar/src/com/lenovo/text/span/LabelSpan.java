package com.lenovo.text.span;

import java.util.Map;

import com.dilapp.radar.textbuilder.utils.L;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class LabelSpan extends ClickableSpan {

	private Map<String, String> params;

	public LabelSpan(Map<String, String> params) {
		this.params = params;
	}

	@Override
	public void onClick(View widget) {

	}

	@Override
	public void updateDrawState(TextPaint ds) {
		if (params.containsKey("color")) {
			int color = Color.parseColor(params.get("color"));
			ds.setColor(color);
		} else {
			ds.setColor(ds.linkColor);
		}
		ds.setUnderlineText(false);
	}

}
