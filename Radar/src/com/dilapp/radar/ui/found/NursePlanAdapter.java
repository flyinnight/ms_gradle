package com.dilapp.radar.ui.found;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.SolutionDetails.SolutionResp;
import com.dilapp.radar.ui.topic.TopicHelper;
import com.dilapp.radar.util.HttpConstant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class NursePlanAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<SolutionResp> mData;
	private LayoutInflater mInflater;
	private ImageLoadingListener animateFirstListener = new NursePlanDisplayListener();
	private DisplayImageOptions options;
	private float rating;
	private String title, topicTitle, postTitle;
	private static final List<String> displayedImages = Collections
			.synchronizedList(new LinkedList<String>());

	public NursePlanAdapter(Context mContext) {
		this.mContext = mContext;
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

	public void setData(ArrayList<SolutionResp> data) {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.nurse_plan_item2, null);
			holder.iv_header = (ImageView) convertView
					.findViewById(R.id.iv_user_header);
			holder.iv_level_image = (ImageView) convertView
					.findViewById(R.id.iv_level_image);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_title);
			holder.rb_ratingBar = (RatingBar) convertView
					.findViewById(R.id.rg_ratingBar);
			holder.tv_level_fraction = (TextView) convertView
					.findViewById(R.id.tv_level_fraction);
			holder.tv_level = (TextView) convertView
					.findViewById(R.id.tv_level);
			holder.tv_userName = (TextView) convertView
					.findViewById(R.id.tv_userName);
			holder.tv_use_count = (TextView) convertView
					.findViewById(R.id.tv_use_count);
			holder.tv_collect_count = (TextView) convertView
					.findViewById(R.id.tv_collect_count);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		if (mData.get(position) == null)
			return convertView;
		// ImageLoader.getInstance().loadImage(
		// HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP+mData.get(position).getThumbString()[0],
		// options,
		// new SimpleImageLoadingListener() {
		// @Override
		// public void onLoadingComplete(String imageUri, View view,
		// Bitmap loadedImage) {
		// super.onLoadingComplete(imageUri, view, loadedImage);
		// holder.iv_header.setImageBitmap(loadedImage);
		// if (loadedImage != null) {
		// ImageView imageView = (ImageView) view;
		// boolean firstDisplay = !displayedImages
		// .contains(imageUri);
		// if (firstDisplay) {
		// FadeInBitmapDisplayer.animate(imageView, 500);
		// displayedImages.add(imageUri);
		// }
		// }
		// }
		// });
		if (!TextUtils.isEmpty(mData.get(position).getPortrait()))
			TopicHelper.setImageFromUrl(
					HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP
							+ mData.get(position).getPortrait(),
					holder.iv_header);
		// holder.tv_topic_title.setText("【" +
		// mData.get(position).getTopicTitle()
		// + "】");
		postTitle = mData.get(position).getPostTitle();
		if (TextUtils.isEmpty(mData.get(position).getTopicTitle())) {
			title = postTitle;
			holder.tv_title.setText(title);
		} else {
			topicTitle = mContext.getResources().getString(
					R.string.topic_prefix, mData.get(position).getTopicTitle());
			title = topicTitle + postTitle;
			Spannable s = new SpannableStringBuilder(title);
			s.setSpan(
					new ForegroundColorSpan(mContext.getResources().getColor(
							R.color.test_primary)), 0, topicTitle.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.tv_title.setText(s);
		}
		holder.tv_userName
				.setText("创建人:  " + mData.get(position).getUserName());
		holder.tv_level_fraction.setText(mData.get(position).getScores() + "分");
		rating = (float) mData.get(position).getScores();
		holder.rb_ratingBar.setRating(rating);
		holder.tv_use_count.setText("喜欢  " + mData.get(position).getFavorite());
		holder.tv_collect_count.setText("收藏  "
				+ mData.get(position).getStoreupNum());
		if (position == 0) {
			holder.iv_level_image.setImageResource(R.drawable.rankings_one);
			holder.iv_level_image.setVisibility(View.VISIBLE);
		} else if (position == 1) {
			holder.iv_level_image.setImageResource(R.drawable.rankings_two);
			holder.iv_level_image.setVisibility(View.VISIBLE);
		} else if (position == 2) {
			holder.iv_level_image.setImageResource(R.drawable.rankings_three);
			holder.iv_level_image.setVisibility(View.VISIBLE);
		} else {
			holder.iv_level_image.setVisibility(View.GONE);
			holder.tv_level.setVisibility(View.VISIBLE);
			holder.tv_level.setText(String.valueOf(position + 1));
		}
		return convertView;
	}

	final class ViewHolder {
		// name
		private TextView tv_userName;
		// 头像
		private ImageView iv_header;
		// 等级图片
		private ImageView iv_level_image;
		// 等级
		private TextView tv_level;
		// 话题标题
		private TextView tv_title;
		// 帖子标题
		private TextView tv_post_title;
		// 评分星星显示
		private RatingBar rb_ratingBar;
		// 评分显示
		private TextView tv_level_fraction;
		// 使用人数
		private TextView tv_use_count;
		// 收藏人数
		private TextView tv_collect_count;
	}

	protected static class NursePlanDisplayListener extends
			SimpleImageLoadingListener {

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {

		}
	}
}
