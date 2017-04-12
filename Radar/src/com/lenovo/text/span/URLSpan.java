package com.lenovo.text.span;

import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextPaint;
import android.view.View;
import android.widget.Toast;

public class URLSpan extends BaseClickSpan {

	private Map<String, String> params;

	public URLSpan(Map<String, String> params) {
		this.params = params;
	}

	@Override
	public void onClick(View widget) {
		/*if (getActivity() == null) {
			Toast.makeText(widget.getContext(), "跳转到浏览器", Toast.LENGTH_SHORT)
					.show();
			return;
		}*/
		if (!params.containsKey("url")) {
			return;
		}
		String url = params.get("url");
		if (url == null || "".equals(url)) {
			Toast.makeText(getActivity(), "地址错误", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			widget.getContext().startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		if (params.containsKey("color")) {
			ds.linkColor = Color.parseColor(params.get("color"));
		}
		ds.setColor(ds.linkColor);
		ds.setUnderlineText(false);
	}

}
