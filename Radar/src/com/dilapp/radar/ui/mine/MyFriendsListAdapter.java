package com.dilapp.radar.ui.mine;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.GetUserRelation.RelationList;
import com.dilapp.radar.ui.topic.TopicHelper;
import com.dilapp.radar.util.MineInfoUtils;

/**
 * 我的发言Adapter
 * 
 * @author Administrator
 * 
 */
public class MyFriendsListAdapter extends BaseAdapter {

	private Context mContext;
	private List<RelationList> mData;
	private String nickName;
	private String getNickName;

	public MyFriendsListAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public void setData(List<RelationList> data) {
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
					R.layout.item_mine_friends_list, null);
			viewHolder.iv_head = (ImageView) convertView
					.findViewById(R.id.iv_head);
			viewHolder.tv_nickname = (TextView) convertView
					.findViewById(R.id.tv_nickname);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();

		if (mData.get(position) == null)
			return convertView;
		MineInfoUtils.setImage(mData.get(position).getPortrait(),
				viewHolder.iv_head);
		nickName = mData.get(position).getName();
		if (TextUtils.isEmpty(nickName))
			viewHolder.tv_nickname
					.setText(mContext.getString(R.string.unknown));
		else
			viewHolder.tv_nickname.setText(nickName);

		return convertView;
	}

	private class ViewHolder {
		private ImageView iv_head;
		private TextView tv_nickname;
	}
}
