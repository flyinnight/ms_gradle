package com.dilapp.radar.ui.mine;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.TitleView;

public class ActivityEditNickName extends BaseActivity implements OnClickListener{
	
	private TitleView mTitle;
	private ImageButton mBtnClear;
	private EditText mNickEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_nick_name);
		
		View title = findViewById(R.id.vg_title);
		mTitle = new TitleView(this, title);
		mTitle.setLeftText(R.string.cancel, this);
		mTitle.setCenterText(R.string.change_nickname, null);
		mTitle.setRightText(R.string.save, this);
		
		mBtnClear = findViewById_(R.id.button_clear);
		mBtnClear.setOnClickListener(this);
		
		mNickEdit = findViewById_(R.id.input_nickname);
		String mNickName = SharePreCacheHelper.getNickName(this);
		if(!TextUtils.isEmpty(mNickName)){
			mNickEdit.setText(mNickName);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.vg_left:
			finish();
			break;
		case R.id.vg_right:
			String nickname = mNickEdit.getEditableText().toString();
			if(!TextUtils.isEmpty(nickname)){
				SharePreCacheHelper.setNickName(getApplicationContext(), nickname);
				setResult(RESULT_OK);
				finish();
			}else{
				Toast.makeText(getApplicationContext(), "昵称不能为空", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.button_clear:
			mNickEdit.setText("");
			break;
		}
	}

}
