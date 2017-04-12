package com.dilapp.radar.ui.mine;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.ui.topic.TopicHelper;

/**
 * 我的发言Adapter
 * 
 * @author Administrator
 * 
 */
public class MySpeakAdapter extends BaseAdapter {

	private Context mContext;
	private List<MPostResp> mData;
	private String nickName;
	private String getNickName;

	public MySpeakAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public void setData(List<MPostResp> data) {
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
					R.layout.my_speak_item, null);
			viewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.tv_text = (TextView) convertView
					.findViewById(R.id.tv_text);
			viewHolder.tv_time = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.tv_locks = (TextView) convertView
					.findViewById(R.id.tv_locks);
			viewHolder.tv_like = (TextView) convertView
					.findViewById(R.id.tv_like);
			viewHolder.tv_reply = (TextView) convertView
					.findViewById(R.id.tv_reply);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();

		if (mData.get(position) == null)
			return convertView;
		viewHolder.tv_text.setText(mData.get(position).getPostTitle());
		viewHolder.tv_name.setText(mData.get(position).getUserName());
		getNickName = mData.get(position).getUserName();
		if (TextUtils.isEmpty(getNickName)) {
			nickName = SharePreCacheHelper.getNickName(mContext);
			if (!TextUtils.isEmpty(nickName)) {
				viewHolder.tv_name.setText(nickName);
			} else {
				viewHolder.tv_name.setText(mContext.getResources().getString(
						R.string.unknown));
			}
		} else {
			viewHolder.tv_name.setText(getNickName);
		}

		String datetime = TopicHelper
				.getTopicDateString(mContext, System.currentTimeMillis(), mData
						.get(position).getUpdateTime());
		viewHolder.tv_time.setText(datetime);
		viewHolder.tv_reply.setText(mData.get(position).getTotalFollows() + "");
		viewHolder.tv_like.setText(mData.get(position).getLike() + "");
		viewHolder.tv_locks
				.setText(mData.get(position).getPostViewCount() + "");
		return convertView;
	}

	private class ViewHolder {
		private TextView tv_text;
		private TextView tv_name;
		private TextView tv_time;
		private TextView tv_reply;
		private TextView tv_like;
		private TextView tv_locks;
	}
}
