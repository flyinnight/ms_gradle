package com.dilapp.radar.ui.found;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.dilapp.radar.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyTopicAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<HashMap<String, Object>> mData;
	private LayoutInflater mInflater;
	private DisplayImageOptions options;
	private static final List<String> displayedImages = Collections
			.synchronizedList(new LinkedList<String>());

	public MyTopicAdapter(Context mContext,
			ArrayList<HashMap<String, Object>> data) {
		this.mContext = mContext;
		this.mData = data;
		mInflater = (LayoutInflater) mContext
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				// 正在加载的图片
				.showImageForEmptyUri(R.drawable.ic_launcher)
				// URL请求失败
				.showImageOnFail(R.drawable.ic_launcher)
				// 图片加载失败
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
	}

	@Override
	public int getCount() {
		return mData.size();
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
			convertView = mInflater
					.inflate(R.layout.found_all_topic_item, null);
			holder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_topic_title);
			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
//			holder.tv_time = (TextView) convertView
//					.findViewById(R.id.tv_release_time);
			holder.tv_join_count = (TextView) convertView
					.findViewById(R.id.tv_join_count);
			holder.iv_header = (ImageView) convertView
					.findViewById(R.id.iv_user_header);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ImageLoader.getInstance().loadImage(
				(String) mData.get(position).get("header"), options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						super.onLoadingComplete(imageUri, view, loadedImage);
						holder.iv_header.setImageBitmap(loadedImage);
						if (loadedImage != null) {
							ImageView imageView = (ImageView) view;
							boolean firstDisplay = !displayedImages
									.contains(imageUri);
							if (firstDisplay) {
								FadeInBitmapDisplayer.animate(imageView, 500);
								displayedImages.add(imageUri);
							}
						}
					}
				});

		holder.tv_name.setText((CharSequence) mData.get(position).get("name"));
		holder.tv_content.setText((CharSequence) mData.get(position).get(
				"content"));
		holder.tv_time.setText(mData.get(position).get("time") + "");
		holder.tv_join_count.setText((CharSequence) mData.get(position).get(
				"join"));

		return convertView;
	}

	final class ViewHolder {
		// 头像
		private ImageView iv_header;
		// 话题标题
		private TextView tv_name;
		// 内容
		private TextView tv_content;
		// 发布时间
		private TextView tv_time;
		// 参与人数
		private TextView tv_join_count;
	}
}
