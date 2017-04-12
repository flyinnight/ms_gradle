package com.dilapp.radar.ui.found;

import static com.dilapp.radar.textbuilder.utils.L.i;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.FollowupPostTopic;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.util.MineInfoUtils;

public class AllTopicAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<MTopicResp> mData;
	private LayoutInflater mInflater;
	private String imageUrl;

	public AllTopicAdapter(Context mContext) {
		this.mContext = mContext;
		mInflater = (LayoutInflater) mContext
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<MTopicResp> data) {
		this.mData = data;
	}

	public List<MTopicResp> getData() {
		return mData;
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public MTopicResp getItem(int position) {
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
			convertView = mInflater
					.inflate(R.layout.found_all_topic_item, null);
			holder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_topic_title);
			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			holder.tv_join_count = (TextView) convertView
					.findViewById(R.id.tv_join_count);
			holder.tv_participation = (TextView) convertView
					.findViewById(R.id.tv_participation);
			holder.iv_header = (ImageView) convertView
					.findViewById(R.id.iv_user_header);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		if (mData.get(position) == null)
			return convertView;
		imageUrl = mData.get(position).getTopicimg()[0];
		if (!TextUtils.isEmpty(imageUrl))
			MineInfoUtils.setImage(imageUrl, holder.iv_header);
		holder.tv_name.setText(mData.get(position).getTopictitle());
		holder.tv_content.setText(mData.get(position).getContent());
		holder.tv_join_count.setText(mData.get(position).getFollowsUpNum()
				+ "人参与");
		final boolean status = mData.get(position).getFollowup();
		Log.i("AllTopicAdapter", "status:" + status);
		if (status) {
			holder.tv_participation
					.setBackgroundResource(R.drawable.bg_text_border_line);
			holder.tv_participation.setText("已参与");
			holder.tv_participation.setTextColor(mContext.getResources()
					.getColor(R.color.hlep_text_normal));
		} else {
			holder.tv_participation.setText("参与");
			holder.tv_participation
					.setBackgroundResource(R.drawable.bg_text_border_fill);
			holder.tv_participation.setTextColor(Color.WHITE);
		}
		holder.tv_participation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				requestFocusTopics(position, mData.get(position).getTopicId(),
						!mData.get(position).getFollowup());
			}
		});
		return convertView;
	}

	final class ViewHolder {
		// 头像
		private ImageView iv_header;
		// 话题标题
		private TextView tv_name;
		// 内容
		private TextView tv_content;
		// // 发布时间
		// private TextView tv_time;
		// 参与人数
		private TextView tv_join_count;
		// 是否参与
		private TextView tv_participation;
	}

	/**
	 * 关注话题
	 * 
	 * @param topicID
	 * @param isFocus
	 * @param clickView
	 */

	private void requestFocusTopics(final int position, long topicID,
			final boolean isFocus) {
		if (topicID == 0) {
			Toast.makeText(mContext, R.string.topic_please_choice_topics,
					Toast.LENGTH_SHORT).show();
			return;
		}
		FollowupPostTopic fpt = ReqFactory.buildInterface(mContext,
				FollowupPostTopic.class);
		FollowupPostTopic.FollowupTopicReq req = new FollowupPostTopic.FollowupTopicReq();
		req.setTopicId(topicID);
		req.setFollowup(isFocus);
		i("III_logic", "topic id " + topicID + ", focus " + isFocus);
		fpt.followupTopicAsync(req, new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					Toast.makeText(
							mContext,
							isFocus ? R.string.found_focus_success
									: R.string.found_detail_cancel_success,
							Toast.LENGTH_SHORT).show();
					// btn_join.setText(!isFocus ? R.string.found_detail_join
					// : R.string.found_detail_unjoin);
					mData.get(position).setFollowup(isFocus);
					AllTopicAdapter.this.notifyDataSetChanged();
					i("III", "话题关系操作成功 " + isFocus + " ");
				} else {
					Toast.makeText(
							mContext,
							isFocus ? R.string.found_focus_failure
									: R.string.found_detail_focus_failure,
							Toast.LENGTH_SHORT).show();
					i("III",
							"话题关系操作失败 " + isFocus + " "
									+ (resp != null ? resp.getMessage() : null));
				}
			}
		});
	}
}
