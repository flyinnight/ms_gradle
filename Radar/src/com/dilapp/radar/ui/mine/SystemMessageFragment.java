package com.dilapp.radar.ui.mine;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseFragment;

/**
 * 系统消息
 * 
 * @author Administrator
 * 
 */
public class SystemMessageFragment extends BaseFragment implements
		OnItemClickListener {
	private final int REQUEST_SUCCESS = 111;
	private ListView listView;
	private RelativeLayout defult_layout;
	private TextView tv_defult;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REQUEST_SUCCESS:
				break;
			default:
				break;
			}
		}
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setContentView(R.layout.mine_conversation_history);
		setCacheView(true);
		initView();
		return getContentView();
	}

	private void initView() {
		defult_layout = findViewById(R.id.defult_layout);
		tv_defult = findViewById(R.id.tv_defult);
		listView = findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		defult_layout.setVisibility(View.VISIBLE);
		tv_defult.setText("您还没有收到系统消息");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}

}
