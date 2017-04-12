package com.dilapp.radar.ui.mine;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.ui.topic.TopicHelper;

public class MyCommentAdapter extends BaseAdapter {

	private Context mContext;
	private List<MPostResp> mData;
	private List<MPostResp> mParentData;
	private String strContent, content;

	public MyCommentAdapter(Context mContext) {
		this.mContext = mContext;
	}
	
	public void setData( List<MPostResp> parentData,
			List<MPostResp> data){
		this.mData = data;
		this.mParentData = parentData;
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
					R.layout.my_comment_item, null);
			viewHolder.tv_text_reply = (TextView) convertView
					.findViewById(R.id.tv_text_reply);
			viewHolder.tv_reply_date = (TextView) convertView
					.findViewById(R.id.tv_reply_date);
			viewHolder.tv_topic_title = (TextView) convertView
					.findViewById(R.id.tv_topic_title);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();

		if (mData.get(position) == null)
			return convertView;
		strContent = mData.get(position).getPostContent();
		if (strContent.startsWith("[{")) {
			if (!TextUtils.isEmpty(strContent)) {
				try {
					JSONArray jsonArray = new JSONArray(strContent.toString());
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						content = jsonObject.getString("content");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				viewHolder.tv_text_reply.setText(content);
			}
		} else {
			viewHolder.tv_text_reply.setText(strContent);
		}
		Log.i("MyCommentAdapter", "strContent:--" + strContent);
		String datetime = TopicHelper
				.getTopicDateString(mContext, System.currentTimeMillis(), mData
						.get(position).getUpdateTime());
		viewHolder.tv_reply_date.setText(datetime);
		String postTitle = mParentData.get(position).getPostTitle();
		if (!TextUtils.isEmpty(postTitle))
			viewHolder.tv_topic_title.setText(postTitle);
		return convertView;
	}

	private class ViewHolder {
		private TextView tv_text_reply;
		private TextView tv_reply_date;
		private TextView tv_topic_title;
	}
}
