package com.dilapp.radar.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.PhoneEmailManage;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.ContextState;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.DialogUtils;
import com.dilapp.radar.util.Slog;

public class ActivityBindingEmail extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "ActivityBindingEmail";
	private static final int BINDING_EMAIL_SUCCESS = 1018;
	private static final int BINDING_EMAIL_FAILARE = 1019;
	private static final int SEND_EMAIL_SUCCESS = 1021;
	private static final int SEND_EMAIL_FAILED = 1022;
	private static final int INTO_EMAIL = 1020;
	private static final int CANCEL_ACTIVITY = 1023;
	private Context mContext;
	private TitleView mTitle;
	private ImageButton mBtnClear;
	private EditText et_email;
	private String accountEmail;

	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case SEND_EMAIL_SUCCESS:
				String emailMsg = mContext.getResources().getString(
						R.string.send_binding_eamil, accountEmail);
				DialogUtils.intoEmailDialog(mContext, emailMsg, handler);
				break;
			case SEND_EMAIL_FAILED:
				Toast.makeText(mContext, R.string.binding_failed,
						Toast.LENGTH_SHORT).show();
				DialogUtils.promptInfoDialog(mContext, "邮件发送失败");
				break;
			case BINDING_EMAIL_SUCCESS:
				sendTestifyEmail();
				break;
			case BINDING_EMAIL_FAILARE:
				Toast.makeText(mContext, R.string.binding_failed,
						Toast.LENGTH_SHORT).show();
				DialogUtils.promptInfoDialog(mContext, "绑定邮箱失败");
				break;
			case INTO_EMAIL:
				if (!TextUtils.isEmpty(accountEmail))
					intoMailbox(accountEmail);
				finish();
				break;
			case CANCEL_ACTIVITY:
				finish();
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_binding_email);

		init_view();
	}

	private void init_view() {
		mContext = this;
		View title = findViewById(R.id.vg_title);
		mTitle = new TitleView(this, title);
		mTitle.setCenterText(R.string.binding_email, null);
		mTitle.setRightText(R.string.save, this);
		mTitle.setLeftIcon(R.drawable.btn_back, this);

		mBtnClear = findViewById_(R.id.button_clear);
		mBtnClear.setOnClickListener(this);

		et_email = findViewById_(R.id.et_email);
		String userEmail = SharePreCacheHelper.getBindedEmail(mContext);
		if (!TextUtils.isEmpty(userEmail)) {
			et_email.setText(userEmail);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.vg_left:
			finish();
			break;
		case R.id.vg_right:
			accountEmail = et_email.getEditableText().toString();
			if (!TextUtils.isEmpty(accountEmail)) {
				if (accountEmail.contains("@")) {
					bindingEmail(accountEmail);
				} else {
					Toast.makeText(mContext, R.string.input_correct_email,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(mContext, R.string.input_correct_email,
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.button_clear:
			et_email.setText("");
			break;
		}
	}

	/**
	 * 绑定邮箱
	 */
	private void bindingEmail(String accountEmail) {
		PhoneEmailManage mDetail = ReqFactory.buildInterface(mContext,
				PhoneEmailManage.class);
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				dimessWaitingDialog();
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						handler.sendEmptyMessage(BINDING_EMAIL_SUCCESS);
					} else {
						handler.sendEmptyMessage(BINDING_EMAIL_FAILARE);
					}
				}
				Log.i(TAG,
						"status:" + resp.getStatus() + "---"
								+ resp.getMessage());
			}
		};
		addCallback(node);
		mDetail.bindEmailAsync(accountEmail, node);
		showWaitingDialog((ContextState) null);
	}

	/**
	 * 发送验证邮件到邮箱
	 */
	private void sendTestifyEmail() {
		PhoneEmailManage mDetail = ReqFactory.buildInterface(mContext,
				PhoneEmailManage.class);
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				dimessWaitingDialog();
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						handler.sendEmptyMessage(SEND_EMAIL_SUCCESS);
						Slog.i("邮件已发送" + resp.getMessage());
					} else {
						handler.sendEmptyMessage(SEND_EMAIL_FAILED);
					}
				}
			}
		};
		addCallback(node);
		mDetail.emailVerifyAsync(node);
		showWaitingDialog((ContextState) null);
	}

	private void intoMailbox(String register_email) {
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
			} else if (register_email.contains("sogou")) {
				uri = Uri.parse("http://mail.sogou.com/");
			} else if (register_email.contains("21cn")) {
				uri = Uri.parse("http://mail.21cn.com/");
			} else if (register_email.contains("263")) {
				uri = Uri.parse("http://263.com/");
			}
		}
		intent.setData(uri);
		startActivity(intent);
		finish();
	}
}
