package com.dilapp.radar.ui.mine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.MineInfoUtils;

/**
 * 我的话题Adapter
 * 
 * @author Administrator
 * 
 */
public class MyTopicAdapter extends BaseAdapter {

	private Context mContext;
	private List<MTopicResp> mData;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private String url;
	private String[] images;

	public MyTopicAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public void setData(List<MTopicResp> data) {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_mine_topic_list, null);
			viewHolder.iv_header = (ImageView) convertView
					.findViewById(R.id.iv_header);
			viewHolder.tv_topic_title = (TextView) convertView
					.findViewById(R.id.tv_topic_title);
			viewHolder.tv_topic_content = (TextView) convertView
					.findViewById(R.id.tv_topic_content);
			viewHolder.tv_people_count = (TextView) convertView
					.findViewById(R.id.tv_people_count);
			viewHolder.tv_create_date = (TextView) convertView
					.findViewById(R.id.tv_create_date);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();

		if (mData.get(position) == null)
			return convertView;
		// iconUrl = SharePreCacheHelper.getUserIconUrl(mContext);
		// if (TextUtils.isEmpty(iconUrl)) {
		// viewHolder.iv_header.setImageResource(R.drawable.img_default_head);
		// } else {
		// if (iconUrl.startsWith("http")) {
		// MineInfoUtils.setImageFromUrl(iconUrl, viewHolder.iv_header);
		// } else {
		// MineInfoUtils.setImageFromUrl(
		// HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP + iconUrl,
		// viewHolder.iv_header);
		// }
		// }
		viewHolder.tv_topic_title.setText(mData.get(position).getTopictitle());
		viewHolder.tv_topic_content.setText(mData.get(position).getContent());
		Log.i("MyTopicAdapter", "URL：" + mData.get(position).getTopicimg());
		images = mData.get(position).getTopicimg();
		if (images != null) {
			for (int i = 0; i < images.length; i++) {
				url = images[i];
			}
		}
		if (TextUtils.isEmpty(url)) {
			viewHolder.iv_header.setImageResource(R.drawable.img_default_head);
		} else {
			if (url.startsWith("http")) {
				MineInfoUtils.setImageFromUrl(url, viewHolder.iv_header);
			} else {
				MineInfoUtils.setImageFromUrl(
						HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP + url,
						viewHolder.iv_header);
			}
		}

		viewHolder.tv_people_count.setText(mData.get(position)
				.getFollowsUpNum() + "人参与");
		Date date = new Date(mData.get(position).getReleasetime());
		String createDate = sdf.format(date);
		viewHolder.tv_create_date.setText(createDate);
		return convertView;
	}

	private class ViewHolder {
		private ImageView iv_header;
		private TextView tv_topic_title;
		private TextView tv_topic_content;
		private TextView tv_people_count;
		private TextView tv_create_date;
	}
}
