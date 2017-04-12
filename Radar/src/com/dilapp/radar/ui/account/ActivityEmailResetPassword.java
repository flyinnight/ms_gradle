package com.dilapp.radar.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.util.Slog;

/**
 * 
 * 邮箱找回密码
 * 
 * @author Administrator
 * 
 */
public class ActivityEmailResetPassword extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "ActivityEmailResetPassword";
	private Context mContext;
	private Button btn_next;
	private ImageButton mBtnBack;
	private EditText et_phone;
	private String accountNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_reset_password);
		init_view();
	}

	private void init_view() {
		mContext = this;
		mBtnBack = findViewById_(R.id.button_back);
		mBtnBack.setOnClickListener(this);
		et_phone = (EditText) findViewById(R.id.et_phone);
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);
		btn_next.setClickable(false);
		btn_next.getBackground().setAlpha(120);

		et_phone.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() > 0) {
					btn_next.setClickable(true);
					btn_next.getBackground().setAlpha(225);
				} else {
					btn_next.setClickable(false);
					btn_next.getBackground().setAlpha(120);
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			finish();
			break;
		case R.id.btn_next:
			accountNumber = et_phone.getText().toString().trim();
			if (!TextUtils.isEmpty(accountNumber)) {
				if (isMobileNum(accountNumber)) {
					Intent intent = new Intent(mContext,
							ActivityPhoneResetPassword.class);
					startActivity(intent);
				} else {
					Intent intent1 = new Intent(mContext,
							ActivityEmailRetrieve.class);
					startActivity(intent1);
					Slog.i("你不是手机号！！！！！");
					Toast.makeText(mContext, "你输入的不是手机号", Toast.LENGTH_SHORT).show();
					
				}
			}
			break;
		default:
			break;
		}
	}

	public static boolean isMobileNum(String mobiles) {
		// Pattern p = Pattern
		// .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		// Matcher m = p.matcher(mobiles);
		// System.out.println(m.matches() + "---");
		// return m.matches();
		String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}
}
