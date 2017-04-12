package com.dilapp.radar.ui.account;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseActivity;

/**
 * 
 * 邮箱找回密码页面
 * 
 * @author Administrator
 * 
 */
public class ActivityEmailRetrieve extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "ActivityEmailRetrieve";
	private Context mContext;
	private Button btn_into_email;
	private TextView tv_send_hint;
	private ImageButton mBtnBack;
	private String register_email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_retrieve);
		init_view();
	}

	private void init_view() {
		mContext = this;
		register_email = getIntent().getStringExtra("register_email");
		mBtnBack = findViewById_(R.id.button_back);
		tv_send_hint = (TextView) findViewById(R.id.tv_send_hint);
		btn_into_email = (Button) findViewById(R.id.btn_into_email);
		mBtnBack.setOnClickListener(this);
		btn_into_email.setOnClickListener(this);

		if (!TextUtils.isEmpty(register_email))
			tv_send_hint.setText(getString(R.string.into_email_text,
					register_email));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			finish();
			break;
		case R.id.btn_into_email:
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri uri = null;
			if (!TextUtils.isEmpty(register_email)) {
				if (register_email.contains("163")) {
					uri = Uri.parse("http://mail.163.com/");
				} else if (register_email.contains("139")) {
					uri = Uri.parse("http://mail.139.com/");
				} else if (register_email.contains("189")) {
					uri = Uri.parse("http://mail.189.com/");
				} else if (register_email.contains("126")) {
					uri = Uri.parse("http://mail.126.com/");
				} else if (register_email.contains("sohu")) {
					uri = Uri.parse("http://mail.sohu.com/");
				} else if (register_email.contains("qq")) {
					uri = Uri.parse("http://mail.qq.com/");
				} else if (register_email.contains("tom")) {
					uri = Uri.parse("http://mail.tom.com/");
				} else if (register_email.contains("sina")) {
					uri = Uri.parse("http://mail.sina.com/");
				} else if (register_email.contains("aliyun")) {
					uri = Uri.parse("http://mail.aliyun.com/");
				}else if (register_email.contains("sogou")) {
					uri = Uri.parse("http://mail.sogou.com/");
				}else if (register_email.contains("21cn")) {
					uri = Uri.parse("http://mail.21cn.com/");
				}else if (register_email.contains("263")) {
					uri = Uri.parse("http://263.com/");
				}
			}
			intent.setData(uri);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}

	}

}
