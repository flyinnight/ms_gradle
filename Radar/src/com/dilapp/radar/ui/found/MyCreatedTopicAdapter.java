package com.dilapp.radar.ui.found;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.util.HttpConstant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MyCreatedTopicAdapter extends BaseAdapter {
	protected DisplayImageOptions options;
	private Context mContext = null;
	private LayoutInflater mInflater;
	private List<MTopicResp> alldata = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd HH:mm");

	public MyCreatedTopicAdapter(Context context) {
		this.mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
						// 正在加载的图片
				.showImageForEmptyUri(R.drawable.ic_launcher)
						// URL请求失败
				.showImageOnFail(R.drawable.ic_launcher)
						// 图片加载失败
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY).build();
	}

	public void setData(List<MTopicResp> alldata) {
		this.alldata = alldata;
	}

	@Override
	public int getCount() {
		return alldata == null ? 0 : alldata.size();
	}

	@Override
	public Object getItem(int position) {
		return alldata.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.my_created_topic_item,
					null);
			holder.iv_icon = (ImageView) convertView
					.findViewById(R.id.iv_header);
			holder.tv_topic_title = (TextView) convertView
					.findViewById(R.id.tv_topic_title);
			holder.tv_topic_content = (TextView) convertView
					.findViewById(R.id.tv_topic_content);
			holder.tv_topic_join_count = (TextView) convertView
					.findViewById(R.id.tv_topic_join_count);
			holder.tv_time = (TextView) convertView
					.findViewById(R.id.tv_create_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ImageLoader.getInstance().loadImage(
				HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP
						+ (String) alldata.get(position).getTopicimg()[0],
				options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						super.onLoadingComplete(imageUri, view, loadedImage);
						holder.iv_icon.setImageBitmap(loadedImage);
					}
				});
		holder.tv_topic_title.setText(alldata.get(position).getTopictitle());
		holder.tv_topic_content.setText(alldata.get(position).getContent());
		holder.tv_topic_join_count.setText(alldata.get(position)
				.getFollowsUpNum() + "人参与");
		Date date = new Date(alldata.get(position).getReleasetime());
		holder.tv_time.setText("创建时间  " + sdf.format(date));
		return convertView;
	}

	final class ViewHolder {
		private ImageView iv_icon;
		private TextView tv_topic_title;
		private TextView tv_topic_content;
		private TextView tv_topic_join_count;
		private TextView tv_time;
	}
}
