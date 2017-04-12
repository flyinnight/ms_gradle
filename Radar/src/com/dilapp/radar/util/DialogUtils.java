package com.dilapp.radar.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.mine.ActivityBindingEmail;

/**
 * 自定义弹出框
 * 
 * @author Administrator
 * 
 */
public class DialogUtils {

	/**
	 * 设置提示信息
	 * 
	 * @param context
	 * @param email
	 */
	public static void promptInfoDialog(final Context context,
			final String email) {
		AlertDialog.Builder builder = new Builder(context);
		final AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setContentView(R.layout.dialog_send_email);
		dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		Button btn_confirm = (Button) dialog.findViewById(R.id.dialog_btn_ok);
		TextView desc = (TextView) dialog.findViewById(R.id.dialog_desc);
		desc.setText(email);
		btn_confirm.setText("确认");

		btn_confirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 邮件发送成功并且进入邮箱
	 * 
	 * @param context
	 */
	public static void intoEmailDialog(final Context context,
			final String email, final Handler handler) {
		AlertDialog.Builder builder = new Builder(context);
		final AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setContentView(R.layout.dialog_into_email);
		dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		Button dialog_btn_cancel = (Button) dialog
				.findViewById(R.id.dialog_btn_cancel);
		Button btn_confirm = (Button) dialog.findViewById(R.id.dialog_btn_ok);
		TextView desc = (TextView) dialog.findViewById(R.id.dialog_desc);
		desc.setText(email);
		dialog_btn_cancel.setText("取消");
		btn_confirm.setText("进入邮箱");

		dialog_btn_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				handler.sendEmptyMessage(1023);
			}
		});
		btn_confirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				handler.sendEmptyMessage(1020);
			}
		});
	}

	/**
	 * 删除与某个人的聊天记录
	 * 
	 * @param context
	 */
	public static void deleteChatLogDialog(final Context context,
			final String email, final Handler handler) {
		AlertDialog.Builder builder = new Builder(context);
		final AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setContentView(R.layout.dialog_into_email);
		dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		Button dialog_btn_cancel = (Button) dialog
				.findViewById(R.id.dialog_btn_cancel);
		Button btn_confirm = (Button) dialog.findViewById(R.id.dialog_btn_ok);
		TextView desc = (TextView) dialog.findViewById(R.id.dialog_desc);
		desc.setText(email);
		dialog_btn_cancel.setText("取消");
		btn_confirm.setText("确定");

		dialog_btn_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		btn_confirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				handler.sendEmptyMessage(1021);
				dialog.dismiss();
			}
		});
	}

}
