package com.dilapp.radar.ui.found;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.SolutionDetails.SolutionResp;
import com.dilapp.radar.ui.topic.TopicHelper;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.Slog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class MyNursePlanAdapter extends BaseAdapter {
	public static final String PLAN_SOURCES = "[smile]";
	private Context mContext;
	private List<SolutionResp> mData;
	private LayoutInflater mInflater;
	private DisplayImageOptions options;
	private boolean IsSolution = false;
	private boolean InUsed = false;
	private Handler handler;
	private String postTitle;
	private Spannable ssb;
	private static final List<String> displayedImages = Collections
			.synchronizedList(new LinkedList<String>());

	public MyNursePlanAdapter(Context mContext, Handler handler) {
		this.mContext = mContext;
		this.handler = handler;
		mInflater = (LayoutInflater) mContext
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				// 正在加载的图片
				.showImageForEmptyUri(R.drawable.ic_launcher)
				// URL请求失败
				.showImageOnFail(R.drawable.ic_launcher)
				// 图片加载失败
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();
	}

	public void setData(List<SolutionResp> data) {
		this.mData = data;
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.my_nurse_plan_item, null);
			holder.tv_topic_title = (TextView) convertView
					.findViewById(R.id.tv_topic_title);
			holder.tv_cancel_collect = (TextView) convertView
					.findViewById(R.id.tv_cancel);
			holder.tv_use_count = (TextView) convertView
					.findViewById(R.id.tv_use_count);
			holder.tv_collect_count = (TextView) convertView
					.findViewById(R.id.tv_collect_count);
			holder.iv_header = (ImageView) convertView
					.findViewById(R.id.iv_user_header);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		if (mData.get(position) == null)
			return convertView;
		if (!TextUtils.isEmpty(mData.get(position).getPortrait()))
			TopicHelper.setImageFromUrl(
					HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP
							+ mData.get(position).getPortrait(),
					holder.iv_header);
		Log.i("MyNursePlanAdapter:",
				"护肤方案:" + mData.get(position).getIsSolution() + "使用:"
						+ mData.get(position).getInUsed());
		IsSolution = mData.get(position).getIsSolution();
		InUsed = mData.get(position).getInUsed();
		postTitle = " " + mData.get(position).getPostTitle();
		ssb = new SpannableStringBuilder(PLAN_SOURCES + postTitle);
		// if (IsSolution) {
		// ssb.setSpan(new VerticalImageSpan(mContext, R.drawable.found_skin,
		// ImageSpan.ALIGN_BASELINE), 0, topicStart,
		// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// }
		// if (InUsed) {
		ssb.setSpan(new ImageSpan(mContext, R.drawable.found_use,
				ImageSpan.ALIGN_BASELINE), 0, PLAN_SOURCES.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		holder.tv_topic_title.setText(ssb);
		holder.tv_use_count.setText("喜欢  " + mData.get(position).getFavorite());
		holder.tv_collect_count.setText("收藏  "
				+ mData.get(position).getStoreupNum());
		holder.tv_cancel_collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				int postId = (int) (mData.get(position).getPostId());
				msg.arg1 = postId;
				msg.what = 1231;
				handler.sendMessage(msg);
				Slog.i("：==postId==" + postId);
			}
		});
		return convertView;
	}

	final class ViewHolder {
		// 头像
		private ImageView iv_header;
		private ImageView iv_skin;
		private ImageView iv_use;
		// 话题标题
		private TextView tv_topic_title;
		// 内容
		// private TextView tv_content;
		// 发布时间
		private TextView tv_use_count;
		// 收藏人数
		private TextView tv_collect_count;
		// 取消收藏
		private TextView tv_cancel_collect;
	}
}
