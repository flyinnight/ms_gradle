package com.dilapp.radar.ui.mine;

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
import com.dilapp.radar.domain.GetUserRelation.RelationList;
import com.dilapp.radar.ui.topic.TopicHelper;
import com.dilapp.radar.util.HttpConstant;

public class MyFollowAdapter extends BaseAdapter {

	private Context mContext;
	private List<RelationList> mData;
	private int flag;

	public MyFollowAdapter(Context mContext, List<RelationList> data, int flag) {
		this.mContext = mContext;
		this.mData = data;
		this.flag = flag;
	}

	public List<RelationList> getData() {
		return mData;
	}

	public void setData(List<RelationList> data) {
		this.mData = data;
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public RelationList getItem(int position) {
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
					R.layout.item_focuson_fans, null);
			viewHolder.iv_head = (ImageView) convertView
					.findViewById(R.id.iv_head);
			viewHolder.tv_nickname = (TextView) convertView
					.findViewById(R.id.tv_nickname);
			viewHolder.tv_level = (TextView) convertView
					.findViewById(R.id.tv_grade);
			viewHolder.tv_sex = (TextView) convertView
					.findViewById(R.id.tv_gender);
			viewHolder.btn_click = (TextView) convertView
					.findViewById(R.id.btn_focus);

			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();

		if (mData.get(position) == null)
			return convertView;
		viewHolder.tv_nickname.setText(mData.get(position).getName());
		viewHolder.tv_sex.setText(mData.get(position).getUserId());
		Log.i("MyFollowAdapter:", "Level:" + mData.get(position).getLevel());
		Log.i("MyFollowAdapter:", "Portrait:"
				+ mData.get(position).getPortrait());
		viewHolder.tv_level.setText("LV" + mData.get(position).getLevel() + "");
		if (!TextUtils.isEmpty(mData.get(position).getPortrait()))
			TopicHelper.setImageFromUrl(
					HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP
							+ mData.get(position).getPortrait(),
					viewHolder.iv_head);
		if (flag == 1) {
			viewHolder.btn_click.setVisibility(View.VISIBLE);
			viewHolder.btn_click.setText(mContext.getResources().getString(
					R.string.detail_followed));
		} else if (flag == 2) {
			viewHolder.btn_click.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	private class ViewHolder {
		private ImageView iv_head;
		private TextView tv_nickname;
		private TextView tv_sex;
		private TextView tv_level;
		private TextView tv_focus;
		private TextView tv_fans;
		private TextView btn_click;
	}
}
