package com.dilapp.radar.ui.found;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.MyTopicCallBack;
import com.dilapp.radar.domain.MyTopicCallBack.MyTopicReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.impl.MyTopicCallBackImpl;
import com.dilapp.radar.ui.BaseFragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MyTopicFragment extends LazyFragment {
	private String mItems[] = { "美白峰峰", "分分就好", "天花板繁", "跟我付费", "不算贵吧", "啊扔的发",
			"话题然后", "发我份啊" };
	private LinearLayout mTopic_Tag_Menu;
	private ListView mLv_topic;
	private ArrayList<HashMap<String, Object>> alldata = null;
	// private List<TopicResp> alldata;
	private final int RQUEST_MY_TAG = 13;
	private boolean isPrepared;
	private RadioGroup group;
	private int mPosition = 0;
	private MyTopicAdapter adapter = null;
	/** 是否已被加载过一次，第二次就不再去请求数据了 */
	private boolean mHasLoadedOnce;
	private AsyncTask task=null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setContentView(R.layout.my_topic_fragment);
		setCacheView(true);
		isPrepared = true;
//		lazyLoad();
		// 因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
		ViewGroup parent = (ViewGroup) getContentView().getParent();
		if (parent != null) {
			parent.removeView(getContentView());
		}
		return getContentView();
	}

	/**
	 * 请求话题标签
	 */
	@SuppressWarnings("deprecation")
	private void requestTopicTag() {
		if (mTopic_Tag_Menu != null && mTopic_Tag_Menu.getChildCount() > 0)
			return;
		RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.leftMargin = 10;
		lp.rightMargin = 10;
		lp.topMargin = 10;
		lp.bottomMargin = 10;
		group = new RadioGroup(getActivity());
		for (int i = 0; i < mItems.length - 5; i++) {
			final RadioButton radio = new RadioButton(getActivity());
			radio.setText(mItems[i]);
			radio.setButtonDrawable(android.R.color.transparent);
			radio.setButtonDrawable(null);
			radio.setBackground(getResources().getDrawable(
					R.drawable.radiobutton));
			group.addView(radio, lp);
			group.setOrientation(LinearLayout.VERTICAL);
			if (i == mPosition) {
				radio.setChecked(true);
			}
			final int current_loop = i;
			radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						mPosition = current_loop;
						Log.v("myradars", "选中了=" + current_loop + "==Text:"
								+ radio.getText().toString());
					}
				}
			});
		}
		mTopic_Tag_Menu.addView(group);
	}

	private void getData() {
		for (int i = 0; i < 100; i++) {
			HashMap map1 = new HashMap<String, Object>();
			map1.put("header",
					"http://content.52pk.com/files/100623/2230_102437_1_lit.jpg");
			map1.put("name", "柠檬的功效");
			map1.put("content", "作风绯闻绯闻皇太后路口给偶加  爱的风微风弄了范文芳,发文范围.啊额我分文,发二维.");
			map1.put("time", "1小时前");
			map1.put("join", "138人参与");
			alldata.add(map1);
		}
	}

	// class RequestTopicTask extends AsyncTask<Integer, Integer, String>
	// implements OnClickListener {
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// mTopic_Tag_Menu = (LinearLayout) findViewById(R.id.layout_tags);
	// mLv_topic = (ListView) findViewById(R.id.lv_my_topic);
	// mMyTags = new ArrayList<HashMap<String, Object>>();
	// adapter = new MyTopicAdapter(MyTopicFragment.this.getActivity(),
	// mMyTags);
	// mLv_topic.setAdapter(adapter);
	// }
	//
	// @Override
	// protected String doInBackground(Integer... params) {
	// mMyTags.clear();
	// mMyTags.addAll(getData());
	// requestTopicTag();
	// // TODO:此接口有待测试
	// // Object obj = ReqFactory.buildInterface(getActivity(),
	// // MyTopicCallBack.class);
	// // MyTopicCallBackImpl imp = (MyTopicCallBackImpl) obj;
	// // MyTopicReq req = new MyTopicReq();
	// // imp.myTopicAsync(req, new BaseCall<List<TopicResp>>() {
	// // @Override
	// // public void call(List<TopicResp> resp) {
	// // alldata = resp;
	// // }
	// // });
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	// // mLv_topic.setAdapter(new MyTopicAdapter(MyTopicFragment.this
	// // .getActivity(), alldata));
	// requestTopicTag();
	// adapter.notifyDataSetChanged();
	// }
	//
	// @Override
	// protected void onCancelled() {
	// super.onCancelled();
	// }
	//
	// @Override
	// public void onClick(View v) {
	//
	// }
	// }

	@Override
	protected void lazyLoad() {
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		}
		task=new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				// 显示加载进度对话框

				mTopic_Tag_Menu = (LinearLayout) findViewById(R.id.layout_tags);
				mLv_topic = (ListView) findViewById(R.id.lv_my_topic);
				alldata = new ArrayList<HashMap<String, Object>>();
				adapter = new MyTopicAdapter(
						MyTopicFragment.this.getActivity(), alldata);
				mLv_topic.setAdapter(adapter);

			}

			@Override
			protected Boolean doInBackground(Void... params) {
				if (!isCancelled()) {
					getData();
				}
				return true;
			}

			@Override
			protected void onPostExecute(Boolean isSuccess) {
				if (isCancelled())
					return;
				if (isSuccess) {
					// 加载成功
					// mLv_topic.setAdapter(new
					// MyTopicAdapter(MyTopicFragment.this
					// .getActivity(), alldata));
					mHasLoadedOnce = true;
					requestTopicTag();
					adapter.notifyDataSetChanged();
				} else {
					// 加载失败
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	@Override
	protected void onInvisible() {
		super.onInvisible();
		if (task!=null) {
			task.cancel(true);
		}
	}
}
